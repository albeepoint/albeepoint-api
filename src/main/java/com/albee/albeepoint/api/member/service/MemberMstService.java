package com.albee.albeepoint.api.member.service;
 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albee.albeepoint.api.common.constant.ErrorCode;
import com.albee.albeepoint.api.common.dto.ResultListDto;
import com.albee.albeepoint.api.common.exception.AlbeepointException;
import com.albee.albeepoint.api.member.constant.EnumMember;
import com.albee.albeepoint.api.member.dto.MemberInfoDto;
import com.albee.albeepoint.api.member.dto.MemberMstDto;
import com.albee.albeepoint.api.member.dto.MemberPidDto;
import com.albee.albeepoint.api.member.dto.MemberPidSearchDto;
import com.albee.albeepoint.api.member.dto.MemberReqDto;
import com.albee.albeepoint.api.member.dto.MemberSearchDto;
import com.albee.albeepoint.api.org.dto.OrgSearchDto;
import com.albee.albeepoint.api.org.service.OrgMstService;
import com.albee.albeepoint.api.point.dto.MemberPtDto;
import com.albee.albeepoint.api.point.dto.PtSearchDto;
import com.albee.albeepoint.api.point.service.MemberPtMstService;
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.api.util.EncUtil;
import com.albee.albeepoint.api.util.VdUtil;
import com.albee.albeepoint.mapper.base.t_member_mst.TMemberMst;
import com.albee.albeepoint.mapper.base.t_member_mst.TMemberMstMapper;
import com.albee.albeepoint.mapper.base.t_member_pid.TMemberPid;
import com.albee.albeepoint.mapper.base.t_member_pt_mst.TMemberPtMst;
import com.albee.albeepoint.mapper.base.t_org_mst.TOrgMst;
import com.albee.albeepoint.mapper.member.MemberMstMapper;

import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
 
@Log4j2
@Service
@Transactional
public class MemberMstService {
    @Autowired
    private TMemberMstMapper tMemberMstMapper;

    @Autowired
    private MemberMstMapper mbrMapper;

    @Autowired
    private MemberPidService mbrPidService;

    @Autowired
    private OrgMstService orgService;

    @Autowired
    private MemberPtMstService mbrPtService;

    public void regMemberMst(MemberReqDto reqDto) {
        VdUtil.emptyEc(reqDto.getOrgCd(), ErrorCode.BIZ_ERR_001008, "기관코드 필수 입력");
        VdUtil.emptyEc(reqDto.getMemberId(), ErrorCode.BIZ_ERR_001001, "회원ID 필수 입력");

        TOrgMst orgEty = orgService.getOrgMstWec(new OrgSearchDto(reqDto.getOrgCd()));
        TMemberMst mbrEty = reqDto.getEntity();
        mbrEty.setOrgNo(orgEty.getOrgNo());

        VdUtil.emptyEc(reqDto.getMemberId(), ErrorCode.BIZ_ERR_001001, "회원ID 미입력");

        String memberIdEnc = EncUtil.encryptAes256ByOrgNo(reqDto.getMemberId(), orgEty.getOrgNo());
        log.debug("입력값(평문) : " + reqDto.getMemberId());
        log.debug("암호화 결과 : " + memberIdEnc);
        log.debug("복호화 결과 : " + EncUtil.decryptAes256ByOrgNo(memberIdEnc, orgEty.getOrgNo()));
        mbrEty.setMemberIdEnc(memberIdEnc);

        String memberNmEnc = EncUtil.encryptAes256ByOrgNo(mbrEty.getMemberNm(), orgEty.getOrgNo());
        mbrEty.setMemberNm(memberNmEnc);

        MemberMstDto oldMbrMstDto = mbrMapper.selectMemberMst(reqDto.getMemberSearch());
        VdUtil.ec(VdUtil.isNotEmpty(oldMbrMstDto), ErrorCode.BIZ_ERR_001002, "기등록 회원ID");

        if(VdUtil.isEmpty(mbrEty.getJoinDt())){
            mbrEty.setJoinDt(LocalDateTime.now());
        }

        if(VdUtil.isEmpty(mbrEty.getMemberStsCd())){
            mbrEty.setMemberStsCd(EnumMember.MemberSts.NORMAL.getCode());
        }

        long newMemberNo = mbrMapper.selectSeqMemberMstNo();
        mbrEty.setMemberNo(newMemberNo);


        VdUtil.ec(this.tMemberMstMapper.insert(mbrEty) <= 0, ErrorCode.BIZ_ERR_001138, "회원 등록 실패");
    }

    public void modMemberMst(MemberReqDto reqDto) {
        VdUtil.emptyEc(reqDto.getMemberId(), ErrorCode.BIZ_ERR_001001, "회원ID 미입력");
        VdUtil.emptyEc(reqDto.getOrgCd(), ErrorCode.BIZ_ERR_001008, "기관코드 필수 입력");

        MemberMstDto mbrMstDto = mbrMapper.selectMemberMst(reqDto.getMemberSearch());
        VdUtil.emptyEc(mbrMstDto, ErrorCode.BIZ_ERR_001010, "미등록 회원");

        String memberNmEnc = EncUtil.encryptAes256ByOrgCd(reqDto.getMemberNm(), reqDto.getOrgCd());
        mbrMstDto.setMemberNm(memberNmEnc);
        TMemberMst mbrEty = reqDto.getEntity();
        mbrEty.setMemberNo(mbrMstDto.getMemberNo());
        mbrEty.setOrgNo(mbrMstDto.getOrgNo());
        VdUtil.ec(this.tMemberMstMapper.updateByPrimaryKeySelective(mbrEty) <= 0, 
            ErrorCode.BIZ_ERR_001139, "회원 정보 수정 실패");
    }

    public void modMemberMstLastContact(Long memberNo) {
        TMemberMst mbrEty = new TMemberMst();
        mbrEty.setMemberNo(memberNo);
        mbrEty.setLastContactDt(LocalDateTime.now());
        VdUtil.ec(this.tMemberMstMapper.updateByPrimaryKeySelective(mbrEty) <= 0, 
            ErrorCode.BIZ_ERR_001140, "회원 최종접촉일 수정 실패")    ;
    }

    public MemberMstDto getMemberMst(MemberSearchDto dom) {
        MemberMstDto member = null;

        try {
            member = this.mbrMapper.selectMemberMst(dom);
        }catch(Exception e){
            throw new AlbeepointException(ErrorCode.BIZ_ERR_001000, "회원 조회 중 DB 에러");
        }
        return member;
    }

    public MemberMstDto getMemberMstWec(MemberSearchDto memberSearch) {
        MemberMstDto member = this.getMemberMst(memberSearch);
        VdUtil.emptyEc(member, ErrorCode.BIZ_ERR_001010, "회원정보 없음");
        return member;
    }

    public MemberInfoDto getMemberInfoWithPid(MemberSearchDto memberSearch) {
        VdUtil.emptyEc(memberSearch.getMemberId(), ErrorCode.BIZ_ERR_001001, "회원ID 미입력");
        VdUtil.emptyEc(memberSearch.getOrgCd(), ErrorCode.BIZ_ERR_001008, "기관코드 필수 입력");
        MemberInfoDto memberInfoDto = new MemberInfoDto();
        MemberMstDto member = getMemberMstWec(memberSearch);
        ComUtil.objectCopy(member, memberInfoDto);

        MemberPidSearchDto memberPidSearch = new MemberPidSearchDto();
        memberPidSearch.setOrgNo(member.getOrgNo());
        memberPidSearch.setMemberNo(member.getMemberNo());
        ResultListDto<MemberPidDto> memberPid = mbrPidService.getMemberPidList(memberPidSearch);
        if(VdUtil.isNotEmpty(memberPid.getList()) && (memberPid.getList().size() > 0)){
            memberInfoDto.setPidList(memberPid.getList());
        }

        PtSearchDto ptSearch = new PtSearchDto();
        ptSearch.setMemberNo(member.getMemberNo());
        ResultListDto<MemberPtDto> contPtList = this.mbrPtService.getMemberPtMstList(ptSearch);
        if(VdUtil.isNotEmpty(contPtList.getList()) && (contPtList.getList().size() > 0)){
            memberInfoDto.setContPtList(contPtList.getList());
        }

        return memberInfoDto;
    }

    public MemberInfoDto getMemberInfo(TMemberMst member) {
        MemberInfoDto memberInfoDto = new MemberInfoDto();
        ComUtil.objectCopy(member, memberInfoDto);

        PtSearchDto ptSearch = new PtSearchDto();
        ptSearch.setMemberNo(member.getMemberNo());
        ResultListDto<MemberPtDto> contPtList = this.mbrPtService.getMemberPtMstList(ptSearch);
        if(VdUtil.isNotEmpty(contPtList.getList()) && (contPtList.getList().size() > 0)){
            memberInfoDto.setContPtList(contPtList.getList());
        }

        return memberInfoDto;
    }

    /*
        회원ID 또는 회원식별자로 조회
     */
    public MemberMstDto getMemberMstByIdOrPid(MemberSearchDto memberSearch) {
        MemberMstDto member = mbrMapper.selectMemberMstByIdOrPid(memberSearch);
        return member;
    }

    /*
        회원ID 또는 회원식별자로 조회
     */
    public MemberMstDto getMemberMstByIdOrPidWec(MemberSearchDto memberSearch) {
        VdUtil.emptyEc(memberSearch.getMemberId(), ErrorCode.BIZ_ERR_001001, "회원ID 미입력");
        VdUtil.emptyEc(memberSearch.getOrgCd(), ErrorCode.BIZ_ERR_001008, "기관코드 필수 입력");

        MemberMstDto member = this.mbrMapper.selectMemberMstByIdOrPid(memberSearch);
        VdUtil.emptyEc(member, ErrorCode.BIZ_ERR_001010, "회원정보 없음");

        return member;
    }

    public ResultListDto<MemberMstDto> getMemberMstList(MemberSearchDto dom) {
        ResultListDto<MemberMstDto> result = new ResultListDto<>();
        Long totalCnt = mbrMapper.selectMemberMstListTotalCnt(dom);
        result.setTotalCnt(totalCnt != null ? totalCnt : 0L);
        if(result.getTotalCnt() > 0){
            List<MemberMstDto> list = mbrMapper.selectMemberMstList(dom);
            result.setList(list);
            result.setPageCnt(list != null && list.size() > 0 ? list.size() : 0);
        }
        return result;
    }

}
