package com.albee.albeepoint.api.point.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albee.albeepoint.api.common.dto.ResultListDto;
import com.albee.albeepoint.api.contract.constant.EnumCont;
import com.albee.albeepoint.api.contract.dto.ContRealDto;
import com.albee.albeepoint.api.contract.service.ContMstService;
import com.albee.albeepoint.api.member.dto.MemberMstDto;
import com.albee.albeepoint.api.member.dto.MemberSearchDto;
import com.albee.albeepoint.api.member.service.MemberMstService;
import com.albee.albeepoint.api.point.dto.MemberPtDto;
import com.albee.albeepoint.api.point.dto.PtSearchDto;
import com.albee.albeepoint.api.util.DateUtil;
import com.albee.albeepoint.api.util.StrUtil;
import com.albee.albeepoint.api.util.VdUtil;
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

    public MemberPtMstEntity isuMemberPtMst(MemberPtMstEntity mbrPtEty, IsuReqDto req){
        MemberMstEntity mbrEty = req.getMember();
        Long isuReqPt = req.getIsuReqPt();

        MemberPtDto mbrPtDto = new MemberPtDto(mbrEty.getMemberId(), req.getContNo(), mbrEty.getOrgCd());
        mbrPtDto.setMemberNo(mbrEty.getMemberNo());
        mbrPtDto.setOrgNo(mbrEty.getOrgNo());
        mbrPtDto.setIsuPt(isuReqPt);
        mbrPtDto.setMemberPtNo(VdUtil.isNotEmpty(mbrPtEty) ? mbrPtEty.getMemberPtNo() : null);

        if(VdUtil.isEmpty(mbrPtEty)){
            mbrPtEty = new MemberPtMstEntity(mbrPtDto);
            mbrPtEty.setIsu(isuReqPt);
            mbrPtEty = regMemberPtMst(mbrPtEty);
        }else{
            updateIsuMemberPtMst(mbrPtDto);
            mbrPtEty = getMemberPtMst(new MemberPtSearch(mbrPtDto.getMemberPtNo()));
        }

        return mbrPtEty;
    }

    public void updateIsuMemberPtMst(MemberPtDto mbrPtDto){
        VdUtil.ec(mbrPtMapper.updateIsuMemberPtMst(mbrPtDto) <= 0, BIZ_ERR_001043);
    }

    public void updateIsuCancelMemberPtMst(MemberPtDto mbrPtDto){
        VdUtil.ec(mbrPtMapper.updateIsuCancelMemberPtMst(mbrPtDto) <= 0, BIZ_ERR_001043);
    }

    public void updateUseMemberPtMst(MemberPtDto mbrPtDto){
        VdUtil.ec(mbrPtMapper.updateUseMemberPtMst(mbrPtDto) <= 0, BIZ_ERR_001043);
    }

    public void updateUseCancelMemberPtMst(MemberPtDto mbrPtDto){
        VdUtil.ec(mbrPtMapper.updateUseCancelMemberPtMst(mbrPtDto) <= 0, BIZ_ERR_001043);
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

        ResultListDto<MemberPtMstEntity> listDto = getMemberPtMstList(ptSearch);

        MemberContPtListResIVo mbrContPtListResVo = new MemberContPtListResIVo();
        mbrContPtListResVo.setOrgCd(mbrEty.getOrgCd());
        mbrContPtListResVo.setMemberNo(mbrEty.getMemberNo());
        mbrContPtListResVo.setMemberId(mbrEty.getMemberId());

        List<MemberContPtResIVo> resList = new ArrayList<>();

        if(VdUtil.isNotEmpty(listDto) && VdUtil.isNotEmpty(listDto.getList())){
            mbrContPtListResVo.setTotalContCnt(listDto.getTotalCnt());
            mbrContPtListResVo.setTotalBalPt(listDto.getList().stream().collect(Collectors.summingLong(MemberPtMstEntity::getBalPt)));

            Long totalAblePt = 0L;
            for(MemberPtMstEntity mbrPtEty : listDto.getList()){
                ContMstEntity cont = contService.getContMstWec(new ContSearch(mbrPtEty.getContNo()));
                ContRealDto contRealDto = ptUtilService.getContForTr(cont, mbrPtEty.getOrgNo(), ptSearch.getMemberNo());
                Long tmpAblePt = ptUseUtilService.calcMemberPtMstAblePt(contRealDto, null, mbrEty.getMemberNo(), null);
                totalAblePt = totalAblePt + tmpAblePt; // 사용가능 포인트 계산
                MemberContPtResIVo mbrContPt = new MemberContPtResIVo();
                mbrContPt.setMemberPtNo(mbrPtEty.getMemberPtNo());
                mbrContPt.setContNo(mbrPtEty.getContNo());
                mbrContPt.setContNm(contRealDto.getContNm());
                mbrContPt.setOrgCd(mbrEty.getOrgCd());
                mbrContPt.setMemberId(mbrEty.getMemberId());
                mbrContPt.setAblePt(tmpAblePt);
                mbrContPt.setBalPt(mbrPtEty.getBalPt());
                resList.add(mbrContPt);
            }

            mbrContPtListResVo.setTotalAblePt(totalAblePt);

            // 순서 정렬 : 사용가능PT 많은순 > 잔여PT 많은순 > 최초생성일빠른순
            StrUtil.println(resList, "resList 정렬 전");
            resList.sort(new MemberContPtResVoComparator());
            StrUtil.println(resList, "resList 정렬 후");

            Long rowNum = 0L;
            for(MemberContPtResIVo memberContPtResVo : resList){
                memberContPtResVo.setRowNum(++rowNum);
            }
        }else{
            mbrContPtListResVo.setTotalContCnt(0L);
            mbrContPtListResVo.setTotalAblePt(0L);
            mbrContPtListResVo.setTotalBalPt(0L);
        }

        mbrContPtListResVo.setList(resList);
        return mbrContPtListResVo;
    }


    public MemberPtMstEntity regMemberPtMst(MemberPtMstEntity mbrPtEty) {
        long newMbrPtNo = mbrPtMapper.selectSeqMemberPtMstNo();
        mbrPtEty.setMemberPtNo(newMbrPtNo);
        mbrPtMapper.insertMemberPtMst(mbrPtEty);
        return mbrPtMapper.selectMemberPtMst(new MemberPtSearch(newMbrPtNo));
    }

    public MemberPtMstEntity getMemberPtMst(MemberPtSearch dom) {
        MemberPtMstEntity memberPtMst = mbrPtMapper.selectMemberPtMst(dom);
        return memberPtMst;
    }

    public MemberPtMstEntity getMemberPtMstWec(MemberPtSearch dom) {
        MemberPtMstEntity memberPtMst = getMemberPtMst(dom);
        return (MemberPtMstEntity)VdUtil.emptyEc(memberPtMst, BIZ_ERR_001047);
    }

    public ResultListDto<MemberPtDto> getMemberPtMstList(PtSearchDto dom) {
        ResultListDto<MemberPtDto> result = new ResultListDto<>();
        Long totalCnt = mbrPtMapper.selectMemberPtMstListTotalCnt(dom);
        result.setTotalCnt(totalCnt != null ? totalCnt : 0L);
        if(result.getTotalCnt() > 0){
            List<MemberPtDto> list = mbrPtMapper.selectMemberPtMstList(dom);
            result.setList(list);
            result.setPageCnt(list != null && list.size() > 0 ? list.size() : 0);
        }
        return result;
    }
}
