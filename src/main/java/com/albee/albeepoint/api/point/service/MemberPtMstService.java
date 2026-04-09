package com.albee.albeepoint.api.point.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albee.albeepoint.api.common.constant.ErrorCode;
import com.albee.albeepoint.api.common.dto.ResultListDto;
import com.albee.albeepoint.api.common.service.EncInfoService;
import com.albee.albeepoint.api.contract.constant.EnumCont;
import com.albee.albeepoint.api.contract.dto.ContRealDto;
import com.albee.albeepoint.api.contract.dto.ContSearchDto;
import com.albee.albeepoint.api.contract.service.ContMstService;
import com.albee.albeepoint.api.member.dto.MemberMstDto;
import com.albee.albeepoint.api.member.dto.MemberSearchDto;
import com.albee.albeepoint.api.member.service.MemberMstService;
import com.albee.albeepoint.api.org.service.OrgMstService;
import com.albee.albeepoint.api.point.dto.IsuReqDto;
import com.albee.albeepoint.api.point.dto.MemberContPtListResDto;
import com.albee.albeepoint.api.point.dto.MemberContPtResDto;
import com.albee.albeepoint.api.point.dto.MemberPtDto;
import com.albee.albeepoint.api.point.dto.MemberPtMstDto;
import com.albee.albeepoint.api.point.dto.MemberPtSearchDto;
import com.albee.albeepoint.api.point.dto.PtSearchDto;
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.api.util.DateUtil;
import com.albee.albeepoint.api.util.EncUtil;
import com.albee.albeepoint.api.util.StrUtil;
import com.albee.albeepoint.api.util.VdUtil;
import com.albee.albeepoint.mapper.base.t_cont_mst.TContMst;
import com.albee.albeepoint.mapper.base.t_member_mst.TMemberMst;
import com.albee.albeepoint.mapper.base.t_member_pt_mst.TMemberPtMst;
import com.albee.albeepoint.mapper.base.t_org_mst.TOrgMstMapper;
import com.albee.albeepoint.mapper.point.MemberPtMstMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
 
@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class MemberPtMstService {
    @Autowired
    private OrgMstService orgMstService;

    @Autowired
    private EncInfoService encInfoService;

    @Autowired
    private ContMstService contService;
    
    @Autowired
    private MemberPtMstMapper mbrPtMapper;
    
    @Autowired
    private MemberMstService mbrService;
    
    @Autowired
    private PtUtilService ptUtilService;
    
    @Autowired
    private PtMstService ptMstService;

    @Autowired
    private PtUseUtilService ptUseUtilService; 

    /* 회원포인트 발급 */
    /* 회원아이디에 해당하는 회원에게 특정 계약, 특정 지점을 대상으로 포인트 발급 처리.
        회원정보 조회->계약정보조회->회원포인트발급처리 */
    public MemberPtMstDto isuMemberPtMst(TMemberPtMst mbrPtEty, IsuReqDto reqDto){
        MemberMstDto mbrMstDto = this.mbrService.getMemberMst(new MemberSearchDto(reqDto.getMemberId()));
 
        Long isuReqPt = reqDto.getIsuReqPt();

        MemberPtDto mbrPtDto = new MemberPtDto(mbrMstDto.getMemberId(), reqDto.getContNo(), mbrMstDto.getOrgCd());
        mbrPtDto.setMemberNo(mbrMstDto.getMemberNo());
        mbrPtDto.setOrgNo(mbrMstDto.getOrgNo());
        mbrPtDto.setIsuPt(isuReqPt);
        mbrPtDto.setMemberPtNo(VdUtil.isNotEmpty(mbrPtEty) ? mbrPtEty.getMemberPtNo() : null);

        

        if(VdUtil.isEmpty(mbrPtEty)){
            mbrPtEty = new TMemberPtMst(mbrPtDto);
            mbrPtEty.setIsu(isuReqPt);
            mbrPtEty = this.regMemberPtMst(mbrPtEty);
        }else{
            updateIsuMemberPtMst(mbrPtDto);
            mbrPtEty = this.getMemberPtMst(new MemberPtSearchDto(mbrPtDto.getMemberPtNo()));
        }

        return mbrPtEty;
    }

    public void updateIsuMemberPtMst(MemberPtDto mbrPtDto){
        VdUtil.ec(mbrPtMapper.updateIsuMemberPtMst(mbrPtDto) <= 0, ErrorCode.BIZ_ERR_001043);
    }

    public void updateIsuCancelMemberPtMst(MemberPtDto mbrPtDto){
        VdUtil.ec(mbrPtMapper.updateIsuCancelMemberPtMst(mbrPtDto) <= 0, ErrorCode.BIZ_ERR_001043);
    }

    public void updateUseMemberPtMst(MemberPtDto mbrPtDto){
        VdUtil.ec(mbrPtMapper.updateUseMemberPtMst(mbrPtDto) <= 0, ErrorCode.BIZ_ERR_001043);
    }

    public void updateUseCancelMemberPtMst(MemberPtDto mbrPtDto){
        VdUtil.ec(mbrPtMapper.updateUseCancelMemberPtMst(mbrPtDto) <= 0, ErrorCode.BIZ_ERR_001043);
    }


    // 회원 계약 포인트 현황 조회 (사용가능 포인트 포함)
    public MemberContPtListResDto getMemberContPt(PtSearchDto ptSearch){
        ptSearch.setBaseDt(DateUtil.getTodayString());
        ptSearch.setContStsCd(EnumCont.ContSts.NORMAL);
        ptSearch.setBaseBalPt(0L);
        ptSearch.setBaseDt(DateUtil.getNowDateTimeString());

        MemberSearchDto search = new MemberSearchDto();
        search.setOrgCd(ptSearch.getOrgCd());
        search.setMemberId(ptSearch.getMemberId());
        MemberMstDto mbrEty = mbrService.getMemberMstByIdOrPidWec(search);
        ptSearch.setMemberNo(mbrEty.getMemberNo());

        ResultListDto<MemberPtMstDto> listDto = getMemberPtMstList(ptSearch);

        MemberContPtListResDto mbrContPtListResDto = new MemberContPtListResDto();
        mbrContPtListResDto.setOrgCd(mbrEty.getOrgCd());
        mbrContPtListResDto.setMemberNo(mbrEty.getMemberNo());
        mbrContPtListResDto.setMemberId(mbrEty.getMemberId());

        List<MemberPtDto> resList = new ArrayList<>();

        if(VdUtil.isNotEmpty(listDto) && VdUtil.isNotEmpty(listDto.getList())){
            mbrContPtListResDto.setTotalContCnt(listDto.getTotalCnt());
            mbrContPtListResDto.setTotalBalPt(listDto.getList().stream().collect(Collectors.summingLong(MemberPtMstDto::getBalPt)));

            Long totalAblePt = 0L;
            for(MemberPtMstDto mbrPtEty : listDto.getList()){
                TContMst contMst = this.contService.getContMstWec(mbrPtEty.getContNo());
                ContRealDto contRealDto = this.ptUtilService.getContForTr(contMst, mbrPtEty.getOrgNo(), ptSearch.getMemberNo());
                Long tmpAblePt = this.ptUseUtilService.calcMemberPtMstAblePt(contRealDto, null, mbrEty.getMemberNo(), null);
                totalAblePt = totalAblePt + tmpAblePt; // 사용가능 포인트 계산
                MemberContPtResDto mbrContPt = new MemberContPtResDto();
                mbrContPt.setMemberPtNo(mbrPtEty.getMemberPtNo());
                mbrContPt.setContNo(mbrPtEty.getContNo());
                mbrContPt.setContNm(contRealDto.getContNm());
                mbrContPt.setOrgCd(mbrEty.getOrgCd());
                mbrContPt.setMemberId(mbrEty.getMemberId());
                mbrContPt.setAblePt(tmpAblePt);
                mbrContPt.setBalPt(mbrPtEty.getBalPt());
                resList.add(mbrContPt);
            }

            mbrContPtListResDto.setTotalAblePt(totalAblePt);

            // 순서 정렬 : 사용가능PT 많은순 > 잔여PT 많은순 > 최초생성일빠른순
            StrUtil.println(resList, "resList 정렬 전");
            resList.sort(new MemberContPtResVoComparator());
            StrUtil.println(resList, "resList 정렬 후");

            Long rowNum = 0L;
            for(MemberContPtResIVo memberContPtResVo : resList){
                memberContPtResVo.setRowNum(++rowNum);
            }
        }else{
            mbrContPtListResDto.setTotalContCnt(0L);
            mbrContPtListResDto.setTotalAblePt(0L);
            mbrContPtListResDto.setTotalBalPt(0L);
        }

        mbrContPtListResDto.setList(resList);
        return mbrContPtListResDto;
    }


    public MemberPtMstDto regMemberPtMst(MemberPtMstEntity mbrPtEty) {
        long newMbrPtNo = mbrPtMapper.selectSeqMemberPtMstNo();
        mbrPtEty.setMemberPtNo(newMbrPtNo);
        mbrPtMapper.insertMemberPtMst(mbrPtEty);
        return this.mbrPtMapper.selectMemberPtMst(new MemberPtSearchDto(newMbrPtNo));
    }

    public MemberPtMstDto getMemberPtMst(MemberPtSearchDto dom) {
        MemberPtMstDto memberPtMst = this.mbrPtMapper.selectMemberPtMst(dom);
        return memberPtMst;
    }

    public MemberPtMstDto getMemberPtMstWec(MemberPtSearchDto dom) {
        MemberPtMstDto memberPtMst = this.getMemberPtMst(dom);
        return (MemberPtMstDto)VdUtil.emptyEc(memberPtMst, ErrorCode.BIZ_ERR_001047);
    }

    public ResultListDto<MemberPtMstDto> getMemberPtMstList(PtSearchDto dom) {
        ResultListDto<MemberPtMstDto> result = new ResultListDto<>();
        Long totalCnt = mbrPtMapper.selectMemberPtMstListTotalCnt(dom);
        result.setTotalCnt(totalCnt != null ? totalCnt : 0L);
        if(result.getTotalCnt() > 0){
            List<MemberPtMstDto> list = this.mbrPtMapper.selectMemberPtMstList(dom);
            result.setList(list);
            result.setPageCnt(list != null && list.size() > 0 ? list.size() : 0);
        }
        return result;
    }
}
