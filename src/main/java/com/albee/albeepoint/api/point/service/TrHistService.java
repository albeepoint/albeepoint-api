package com.albee.albeepoint.api.point.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albee.albeepoint.api.common.constant.ErrorCode;
import com.albee.albeepoint.api.common.dto.ResultListDto;
import com.albee.albeepoint.api.point.dto.TrHistDto;
import com.albee.albeepoint.api.point.dto.TrHistRegReqDto;
import com.albee.albeepoint.api.point.dto.TrHistSearchDto;
import com.albee.albeepoint.api.util.VdUtil;
import com.albee.albeepoint.mapper.base.t_tr_hist.TTrHist;
import com.albee.albeepoint.mapper.base.t_tr_hist.TTrHistMapper;
import com.albee.albeepoint.mapper.point.MemberPtMstMapper;
import com.albee.albeepoint.mapper.point.PtMstMapper;
import com.albee.albeepoint.mapper.point.TrHistDetailMapper;
import com.albee.albeepoint.mapper.point.TrHistMapper;

import java.util.List;
 
@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class TrHistService {
    @Autowired
    private TTrHistMapper tTrHistMapper;
    @Autowired
    private TrHistMapper thMapper;
    @Autowired
    private TrHistDetailMapper thdMapper;
    @Autowired
    private PtMstMapper ptMapper;
    @Autowired
    private MemberPtMstMapper mbrPtMapper;

    public void regTrHist(TrHistRegReqDto dom){
        TTrHist trHist = new TTrHist();
        trHist.setTrSno(dom.getTrSno());
        trHist.setTrGbCd(dom.getTrGbCd().getCode());
        // trHist.setContNo(dom.getContNo());
        trHist.setOrgNo(dom.getOrgNo());
        trHist.setBrchNo(dom.getBrchNo());
        trHist.setOrglTrSno(dom.getOrglTrSno());
        // trHist.setPtNo(dom.getPtNo());
        // trHist.setMemberPtNo(dom.getMemberPtNo());
        trHist.setTrMethodGbCd(dom.getTrMethodGbCd());  
        VdUtil.ec(this.tTrHistMapper.insert(trHist) < 1, ErrorCode.BIZ_ERR_001046);
    }


    public TrHistDto getLastOneTrHist(TrHistSearchDto dom) {
        return this.thMapper.selectLastOneTrHist(dom);
    }

    public TTrHist getTrHist(Long trSno) {
        return this.tTrHistMapper.selectByPrimaryKey(trSno);
    }

    public ResultListDto<TrHistDto> getTrHistList(TrHistSearchDto dom) {
        ResultListDto<TrHistDto> result = new ResultListDto<>();
        Long totalCnt = thMapper.selectTrHistListTotalCnt(dom);
        result.setTotalCnt(totalCnt != null ? totalCnt : 0L);
        if(result.getTotalCnt() > 0){
            List<TrHistDto> list = thMapper.selectTrHistList(dom);
            result.setList(list);
            result.setPageCnt(list != null && list.size() > 0 ? list.size() : 0);
        }
        return result;
    }


}
