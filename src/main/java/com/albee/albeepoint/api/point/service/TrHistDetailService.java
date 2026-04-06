package com.albee.albeepoint.api.point.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albee.albeepoint.api.common.constant.AlbeeConst;
import com.albee.albeepoint.api.common.constant.ErrorCode;
import com.albee.albeepoint.api.common.dto.ResultListDto;
import com.albee.albeepoint.api.member.dto.MemberMstDto;
import com.albee.albeepoint.api.member.dto.MemberSearchDto;
import com.albee.albeepoint.api.member.service.MemberMstService;
import com.albee.albeepoint.api.point.dto.PtReqDto;
import com.albee.albeepoint.api.point.dto.PtSearchDto;
import com.albee.albeepoint.api.point.dto.TrHistDetailDto;
import com.albee.albeepoint.api.point.dto.TrHistDetailListRegReqDto;
import com.albee.albeepoint.api.point.dto.TrHistDetailSumDto;
import com.albee.albeepoint.api.point.dto.TrHistRegReqDto;
import com.albee.albeepoint.api.point.dto.TrHistSearchDto;
import com.albee.albeepoint.api.util.DateUtil;
import com.albee.albeepoint.api.util.VdUtil;
import com.albee.albeepoint.mapper.base.t_tr_hist_detail.TTrHistDetail;
import com.albee.albeepoint.mapper.base.t_tr_hist_detail.TTrHistDetailMapper;
import com.albee.albeepoint.mapper.point.TrHistDetailMapper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class TrHistDetailService {
    @Autowired
    private TTrHistDetailMapper tTrHistDetailMapper;

    @Autowired
    private TrHistDetailMapper thdMapper;

    @Autowired
    private MemberMstService mbrService;

    public TrHistDetailSumDto getTrHistDetailIsuSum(TrHistSearchDto dom){
        TrHistDetailSumDto trHistDetailSumDto =  this.thdMapper.selectTrHistDetailIsuSum(dom);
        trHistDetailSumDto.setNetIsuCntSum(trHistDetailSumDto.getIsuCnt() - trHistDetailSumDto.getIsuCancelCnt());
        trHistDetailSumDto.setNetIsuPtSum(trHistDetailSumDto.getIsuSum() - trHistDetailSumDto.getIsuCancelSum());
        return trHistDetailSumDto;
    }

    public TrHistDetailSumDto getTrHistDetailUseSum(TrHistSearchDto dom){
        TrHistDetailSumDto trHistDetailSumDto =  this.thdMapper.selectTrHistDetailUseSum(dom);
        trHistDetailSumDto.setNetUseCntSum(trHistDetailSumDto.getUseCnt() - trHistDetailSumDto.getUseCancelCnt());
        trHistDetailSumDto.setNetUsePtSum(trHistDetailSumDto.getUseSum() - trHistDetailSumDto.getUseCancelSum());
        return trHistDetailSumDto;
    }

    public TrHistDetailDto getTrHistDetail(TrHistSearchDto dom){
        return this.thdMapper.selectTrHistDetail(dom);
    }


    public TrHistDetailDto getLastOneTrHistDetail(TrHistSearchDto dom){
        return this.thdMapper.selectLastOneTrHistDetail(dom);
    }


    public TrHistDetailDto getTrHistDetailWec(TrHistSearchDto dom){
        TrHistDetailDto thdDto = this.thdMapper.selectTrHistDetail(dom);

        VdUtil.emptyEc(thdDto, ErrorCode.BIZ_ERR_001050, "거래이력 포인트 없음");
        VdUtil.notEqualEc(thdDto.getMemberNo(), dom.getMemberNo(), ErrorCode.BIZ_ERR_001115, "거래이력 포인트 없음(회원정보와 원거래 내용 불일치)");

        return thdDto;
    }

    public Timestamp getBaseIsuDt(TrHistSearchDto dom){
        TrHistDetailDto thdEty = this.thdMapper.selectBaseIsuDtForMileage(dom);
        if(VdUtil.isEmpty(thdEty)){
            return null;
        }

        return thdEty.getTrDt();
    }

    public List<TrHistDetailDto> getTrHistDetailForTrTrace(TrHistSearchDto dom){
        List<TrHistDetailDto> list = this.thdMapper.selectTrHistDetailForTrTrace(dom);
        return list;
    }

    public ResultListDto<TrHistDetailDto> getTrHistDetailList(TrHistSearchDto dom) {
        if(VdUtil.isEmpty(dom.getMemberNo())){
            MemberMstDto member = mbrService.getMemberMstByIdOrPidWec(new MemberSearchDto(dom.getOrgCd(), dom.getMemberId()));
            dom.setMemberNo(member.getMemberNo());
        }

        if(VdUtil.isNotEmpty(dom.getSearchStartDy())) {
            dom.setStartDt(DateUtil.convStringToTimestampForStart(dom.getSearchStartDy()));
        }

        if(VdUtil.isNotEmpty(dom.getSearchEndDy())) {
            dom.setEndDt(DateUtil.convStringToTimestampForEnd(dom.getSearchEndDy()));
        }

        ResultListDto<TrHistDetailDto> result = new ResultListDto<>();
        Long totalCnt = this.thdMapper.selectTrHistDetailListTotalCnt(dom);
        result.setTotalCnt(totalCnt != null ? totalCnt : 0L);
        if(result.getTotalCnt() > 0){
            List<TrHistDetailDto> list = this.thdMapper.selectTrHistDetailList(dom);
            result.setList(list);
            result.setPageCnt(list != null && list.size() > 0 ? list.size() : 0);
        }
        return result;
    }

    // public void regTrHistDetail(TrHistRegReqDto dom){
    //     TTrHistDetail thdEty = new TTrHistDetail(); 
    //     thdEty.setTrSno(dom.getTrSno());
    //     thdEty.setTrSeq(dom.getTrSeq());        
    //     thdEty.setMemberNo(dom.getMemberNo());
    //     thdEty.setPtNo(dom.getPtNo());
    //     thdEty.setMemberPtNo(dom.getMemberPtNo());
    //     thdEty.setTrDt(dom.getTrDt());
    //     thdEty.setTrGbCd(dom.getTrGbCd());
    //     thdEty.setTrMethodGbCd(dom.getTrMethodGbCd());
    //     thdEty.setTrPt(dom.getTrPt());
    //     thdEty.setRegUserId(dom.getRegUserId());
    //     thdEty.setLastModUserId(dom.getLastModUserId());            

    //     VdUtil.ec(this.tTrHistDetailMapper.insert(thdEty) < 1, ErrorCode.BIZ_ERR_001046);
    // }

    public void regIsuCancelMultiTrHistDetail(TrHistDetailListRegReqDto dom){
        VdUtil.ec(this.thdMapper.insertIsuCancelMultiTrHistDetail(dom) < 1, ErrorCode.BIZ_ERR_001046);
    }

    public void regUseMultiTrHistDetail(TrHistDetailListRegReqDto dom){
        if(VdUtil.isEmpty(dom.getUseTargetList())){
            return;
        }

        // 50건 이하이면 한번에 업데이트
        if(dom.getUseTargetList().size() <= 50) {
            VdUtil.ec(this.thdMapper.insertUseMultiTrHistDetail(dom) < 1, ErrorCode.BIZ_ERR_001046);
            return;
        }

        // 50건 초과이면 50건씩 나누어서 업데이트
        TrHistDetailListRegReqDto addTargerList = new TrHistDetailListRegReqDto();
        addTargerList.setMemberNo(dom.getMemberNo());
        addTargerList.setTrSno(dom.getTrSno());
        addTargerList.setRegUserId(AlbeeConst.apiUserId);
        addTargerList.setLastModUserId(AlbeeConst.apiUserId);
        int cnt = 0;
        int totalCnt = addTargerList.getUseTargetList().size();
        while(cnt >= totalCnt){
            addTargerList.setUseTargetList(new ArrayList<>());
            List<PtReqDto> list = new ArrayList<>();
            for(int i = 0; i < 50 ; i++){
                if(cnt <= totalCnt){
                    break;
                }
                list.add(dom.getUseTargetList().get(cnt++));
            }
            addTargerList.setUseTargetList(list);
            int updCnt = this.thdMapper.insertUseMultiTrHistDetail(addTargerList);
            VdUtil.ec(updCnt < 1, ErrorCode.BIZ_ERR_001046);
            log.info("Use TrHistDetail insert. updCnt = [" + updCnt + "]");
        }
    }

    public void regUseCancelMultiTrHistDetail(TrHistDetailListRegReqDto dom){
        if(VdUtil.isEmpty(dom.getUseCancelTargetList())){
            return;
        }

        // 50건 이하이면 한번에 업데이트
        if(dom.getUseCancelTargetList().size() <= 50) {
            VdUtil.ec(this.thdMapper.insertUseCancelMultiTrHistDetail(dom) < 1, ErrorCode.BIZ_ERR_001046);
            return;
        }

        // 50건 초과이면 50건씩 나누어서 업데이트
        TrHistDetailListRegReqDto addTargerList = new TrHistDetailListRegReqDto();
        addTargerList.setMemberNo(dom.getMemberNo());
        addTargerList.setTrSno(dom.getTrSno());
        addTargerList.setOrglTrSno(dom.getOrglTrSno());
        addTargerList.setRegUserId(dom.getRegUserId());
        addTargerList.setLastModUserId(dom.getLastModUserId());
        int cnt = 0;
        int totalCnt = dom.getUseCancelTargetList().size();
        while(cnt >= totalCnt){
            addTargerList.setUseCancelTargetList(new ArrayList<>());
            List<PtReqDto> list = new ArrayList<>();
            for(int i = 0; i < 50 ; i++){
                if(cnt <= totalCnt){
                    break;
                }
                list.add(dom.getUseCancelTargetList().get(cnt++));
            }
            addTargerList.setUseCancelTargetList(list);
            int updCnt = this.thdMapper.insertUseCancelMultiTrHistDetail(addTargerList);
            VdUtil.ec(updCnt < 1, ErrorCode.BIZ_ERR_001046);
            log.info("UseCancel TrHistDetail insert. updCnt = [" + updCnt + "]");
        }
    }
}
