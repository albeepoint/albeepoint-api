package com.albee.albeepoint.api.point.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albee.albeepoint.api.common.constant.ErrorCode;
import com.albee.albeepoint.api.contract.constant.EnumCont;
import com.albee.albeepoint.api.contract.dto.ContLocalLimitDto;
import com.albee.albeepoint.api.contract.dto.ContOrgSearchDto;
import com.albee.albeepoint.api.contract.dto.ContRealDto;
import com.albee.albeepoint.api.contract.dto.ContSearchDto;
import com.albee.albeepoint.api.contract.service.ContBrchService;
import com.albee.albeepoint.api.contract.service.ContMstService;
import com.albee.albeepoint.api.member.constant.EnumMember;
import com.albee.albeepoint.api.member.dto.MemberSearchDto;
import com.albee.albeepoint.api.member.service.MemberMstService;
import com.albee.albeepoint.api.org.constant.EnumOrg;
import com.albee.albeepoint.api.org.dto.OrgSearchDto;
import com.albee.albeepoint.api.org.service.BrchMstService;
import com.albee.albeepoint.api.org.service.OrgMstService;
import com.albee.albeepoint.api.point.dto.BasePtReqDto;
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.api.util.DateUtil;
import com.albee.albeepoint.api.util.VdUtil; 

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class PtUtilService {
    @Autowired
    private ContMstService contService;
    @Autowired
    private SubContMstService subContService;
    @Autowired
    private OrgMstService orgService;
    @Autowired
    private BrchMstService brchService;
    @Autowired
    private MemberMstService mbrService;
    @Autowired
    private ContOrgService contOrgService;
    @Autowired
    private ContBrchService contBrchService;

    // 필수 입력 체크 및 회원, 기관, 지점 정보 설정
    public BasePtReqDto checkAndSetBasePtReq(BasePtReqDto req ){
        VdUtil.emptyEc(req.getOrgCd(), ErrorCode.BIZ_ERR_001008, "기관코드 필수 입력");
        VdUtil.emptyEc(req.getMemberId(), ErrorCode.BIZ_ERR_001001, "회원ID 필수 입력");

        // 회원정보, 기관정보, 지점정보 조회
        req.setMember(mbrService.getMemberMstByIdOrPidWec(new MemberSearchDto(req.getOrgCd(), req.getMemberId())));
        req.setOrg(orgService.getOrgMstWec(new OrgSearchDto(req.getOrgCd())));

        if(VdUtil.isNotEmpty(req.getBrchCd())) {
            req.setBrch(brchService.getBrchMstWec(new OrgSearchDto(req.getOrgCd(), req.getBrchCd())));
        }

        if(VdUtil.isNotEmpty(req.getContNo())) {
            req.setCont(contService.getContMstWec(new ContSearchDto(req.getContNo())));
        }
        return req;
    }


    /*
        적립/사용에 적용할 계약 추출(서브 계약 존재시 서브 계약) 및 기관/지점 제약 사항 조회
     */
    public ContRealDto getContForTr(ContMstEntity cont, Long orgNo, Long brchNo) {
        ContRealDto contRealDto = new ContRealDto();

        // 계약정보 설정(서브계약 존재하면 서브계약정보로 설정)
        ContLocalLimitDto localLimit = null;

        // 기본 계약정보 조회. 계약번호, 계약상태, 계약기간
        VdUtil.notEqualEc(cont.getContStsCd(), EnumCont.ContSts.NORMAL, ErrorCode.BIZ_ERR_001018, "계약상태 정상 아님");
        VdUtil.ec(!DateUtil.isBetween(cont.getStartDt(), cont.getEndDt()), ErrorCode.BIZ_ERR_001013, "계약기간 아님");

        /// 서브계약 체크
        // 기관코드, 지점코드 입력시 계약지점 테이블 부터 체크
        // 계약기관 조회 및 설정
        ContOrgEntity contOrgEty = contOrgService.getContOrg(new ContOrgSearchDto(cont.getContNo(), null, orgNo));
        VdUtil.ec(VdUtil.isEmpty(contOrgEty) || VdUtil.isNotEqual(contOrgEty.getContOrgStsCd(), EnumCont.ContOrgSts.NORMAL)
                , ErrorCode.BIZ_ERR_001034, "계약 기관이 아님");
        VdUtil.notEqualEc(contOrgEty.getSaveCanYn(), "Y", ErrorCode.BIZ_ERR_001055, "적립 불가 기관");

        ContBrchEntity contBrchEty = contBrchService.getContBrch(new ContOrgSearchDto(cont.getContNo(), null, orgNo, brchNo));
        if(VdUtil.isNotEmpty(contBrchEty) && VdUtil.isEqual(contBrchEty.getContBrchStsCd(), EnumCont.ContOrgSts.NORMAL)){
            SubContMstEntity subContEty = subContService.getSubContMst(new ContSearchDto(contBrchEty.getContNo(), contBrchEty.getContSeq()));
            CommUtil.objectCopy(subContEty, cont);
            localLimit = new ContLocalLimitDto();
            CommUtil.objectCopy(contBrchEty, localLimit);
            localLimit.setLocalGbCd(EnumOrg.LocalGb.BRCH);
            localLimit.setTotalIsuAbleCnt(contBrchEty.getTotalIsuAbleCnt());
            localLimit.setTotalIsuAblePt(contBrchEty.getTotalIsuAblePt());
        }else{
            // 계약지점 테이블에 없으면 계약기관 테이블 체크
            if(VdUtil.isNotEmpty(contOrgEty) && VdUtil.isEqual(contOrgEty.getContOrgStsCd(), EnumCont.ContOrgSts.NORMAL)){
                SubContMstEntity subContEty = subContService.getSubContMst(new ContSearchDto(contOrgEty.getContNo(), contOrgEty.getContSeq()));
                CommUtil.objectCopy(subContEty, cont);
                localLimit = new ContLocalLimitDto();
                CommUtil.objectCopy(contOrgEty, localLimit);
                localLimit.setLocalGbCd(EnumOrg.LocalGb.ORG);
                localLimit.setTotalIsuAbleCnt(contOrgEty.getTotalIsuAbleCnt());
                localLimit.setTotalIsuAblePt(contOrgEty.getTotalIsuAblePt());
            }
        }

        ComUtil.objectCopy(cont, contRealDto);
        // contRealDto.setCont(cont);

        contRealDto.setContLocalLimit(localLimit);

        return contRealDto;
    }

    /*
        기관정보 조회
     */
    public OrgMstEntity checkOrg(BasePtReqDto req){
        OrgMstEntity orgMstEntity = null;
        OrgSearchDto orgSearch = new OrgSearchDto();

        if(VdUtil.isEmpty(req.getOrg())){
            if(VdUtil.isNotEmpty(req.getOrgNo())){
                orgSearch.setOrgNo(req.getOrgNo());
            }else if(VdUtil.isNotEmpty(req.getOrgCd())){
                orgSearch.setOrgCd(req.getOrgCd());
            }else{
                // 적립요청시 기관코드 미입력시에는 해당 계약의 기등록된 계약기관코드를 조회한다.
                // 계약기관코드가 1건이면 해당 기관에 대해 기관정보를 조회하여 처리한다.
                ResultListDto<ContOrgEntity> contOrgList = contOrgService.getContOrgList(new ContOrgSearchDto(req.getContNo()));
                if(contOrgList != null && contOrgList.getList() != null && contOrgList.getList().size() == 1){
                    orgSearch.setOrgNo(contOrgList.getList().get(0).getOrgNo());
                }else {
                    // 적립요청시 기관코드 미입력인데 등록된 계약기관이 여러개이면 반드시 기관코드를 입력해야 함
                    throw new AlbeepointException(BIZ_ERR_001008, "기관코드 필수 입력");
                }
            }
            orgMstEntity = orgService.getOrgMstWec(orgSearch);
        }
 
        VdUtil.notEqualEc(orgMstEntity.getOrgStsCd(), EnumOrg.OrgSts.NORMAL, BIZ_ERR_001019, "기관상태 정상 아님");

        return orgMstEntity;
    }


    /*
        지점정보 조회
     */
    public BrchMstEntity checkBrch(BasePtReqDto req){
        BrchMstEntity brch = null;
        if(VdUtil.isNotEmpty(req.getOrgCd()) && VdUtil.isNotEmpty(req.getBrchCd())){
            brch = brchService.getBrchMstWec(new OrgSearchDto(req.getOrgCd(), req.getBrchCd()));
            VdUtil.notEqualEc(brch.getBrchStsCd(), EnumOrg.BrchSts.NORMAL, ErrorCode.BIZ_ERR_001020, "지점상태 정상 아님");
        }

        return brch;
    }


    /*
        회원정보 조회
            회원정보 미존재시, 기관정보의 회원자동등록여부가 "Y"이면 지동등록, 아니면 오류 응답
     */
    public MemberMstEntity checkMember(BasePtReqDto req){
        MemberMstEntity member = mbrService.getMemberMst(new MemberSearchDto(req.getMemberId()));
        if(VdUtil.isEmpty(member)){
            if(VdUtil.isNotEqual(req.getOrg().getMemberAutoRegYn(), "Y")) {
                VdUtil.emptyEc(member, BIZ_ERR_001010, "미존재 회원");
            }else{
                member = mbrService.regMemberMst(new MemberReqIVo(req.getOrgCd(), req.getMemberId(), req.getMemberNm()));
            }
        }else{
            if(VdUtil.isNotEmpty(req.getOrgCd()) && VdUtil.isNotEqual(member.getOrgCd(), req.getOrgCd())){
                throw new AlbeepointException(BIZ_ERR_001136, "회원 기관코드와 입력 기관코드 불일치");
            }

            // 존재하는 회원인 경우 기존 회원명과 입력된 회원명 비교하여 다른 경우 업데이트
            if(VdUtil.isNotEmpty(req.getMemberNm())){
                if(VdUtil.isEmpty(member.getMemberNm()) || VdUtil.isNotEqual(member.getMemberNm(), req.getMemberNm())){
                    member = mbrService.modMemberMst(new MemberReqIVo(member.getOrgCd(), member.getMemberId(), req.getMemberNm()));
                }
            }
        }

        VdUtil.notEqualEc(member.getMemberStsCd(), EnumMember.MemberSts.NORMAL, ErrorCode.BIZ_ERR_001020, "회원상태 정상 아님");
        return member;
    }

}
