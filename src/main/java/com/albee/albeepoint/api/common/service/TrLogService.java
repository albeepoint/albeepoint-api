package com.albee.albeepoint.api.common.service;

import com.albee.albeepoint.api.common.dto.ResultListDto;
import com.albee.albeepoint.api.common.dto.TrLogDto;
import com.albee.albeepoint.api.common.dto.TrLogSearchDto;
import com.albee.albeepoint.api.member.dto.MemberMstDto;
import com.albee.albeepoint.api.member.dto.MemberPidSearchDto;
import com.albee.albeepoint.api.member.dto.MemberSearchDto;
import com.albee.albeepoint.api.member.service.MemberMstService;
import com.albee.albeepoint.api.member.service.MemberPidService;
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.api.util.DateUtil;
import com.albee.albeepoint.api.util.EncUtil;
import com.albee.albeepoint.api.util.VdUtil;
import com.albee.albeepoint.api.util.WebUtil; 
import com.albee.albeepoint.mapper.base.t_member_pid.TMemberPid;
import com.albee.albeepoint.mapper.base.t_tr_log.TTrLog;
import com.albee.albeepoint.mapper.base.t_tr_log.TTrLogMapper;
import com.albee.albeepoint.mapper.common.TrLogMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class TrLogService {
    @Autowired
    TTrLogMapper tTrLogMapper;

    @Autowired
    TrLogMapper trLogMapper;
    @Autowired
    MemberMstService memberMstService;
    @Autowired
    MemberPidService memberPidService;

    public TrLogDto regTrLog(HttpServletRequest request, Object reqObj) {
        TrLogDto trLogDto = new TrLogDto(request.getRequestURI(), reqObj);
        trLogDto.setTrDt(DateUtil.getNowDateTimeString());
        trLogDto.setTrId(ComUtil.getUuid());
        trLogDto.setIpAddr(WebUtil.getClientIp(request));

        log.info("from : [" + trLogDto.getIpAddr() + "] request path : [" + trLogDto.getTrGb() + "]");
        log.info("request content : [" + trLogDto.getReqContent() + "]");

        MemberSearchDto mbrSearch = new MemberSearchDto();
        ComUtil.objectCopy(reqObj, mbrSearch);
        if(VdUtil.isNotEmpty(mbrSearch.getMemberId()) && VdUtil.isNotEmpty(mbrSearch.getOrgCd())){
            trLogDto.setMemberIdEnc(EncUtil.encryptAes256ByOrgCd(mbrSearch.getMemberId(), mbrSearch.getOrgCd()));
        }

        this.tTrLogMapper.insert(trLogDto.getEntity());
        return trLogDto;
    }

    public void modTrLog(TrLogDto dom, Object resObj) {
        try {
            dom.setResContent((new ObjectMapper()).writeValueAsString(resObj));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        this.tTrLogMapper.updateByPrimaryKey(dom.getEntity());
    }

    public void delTrLog(String trDt, String trId) {
        this.tTrLogMapper.deleteByPrimaryKey(trDt, trId);
        return;
    }

    public TTrLog getTrLog(String trDt, String trId) {
        return tTrLogMapper.selectByPrimaryKey(trDt, trId);
    }

    public ResultListDto<TTrLog> getTrLogList(TrLogSearchDto dom) {
        ResultListDto<TTrLog> result = new ResultListDto<>();
        Long totalCnt = trLogMapper.selectTrLogListTotalCnt(dom);
        result.setTotalCnt(totalCnt != null ? totalCnt : 0L);
        if(result.getTotalCnt() > 0){
            List<TTrLog> list = trLogMapper.selectTrLogList(dom);
            result.setList(list);
            result.setPageCnt(list != null && list.size() > 0 ? list.size() : 0);
        }
        return result;
    }

    private Long searchMemberMst(MemberSearchDto mbrSearch){
        Long memberNo = null;

        // 기본적으로 OrgCd 는 필수적으로 유입되어야 함.
        // memberId, pid 는 OrgCd(OrgNo) 와 함께 유니크하기 때문..
        if(VdUtil.isNotEmpty(mbrSearch.getOrgCd())) {
            if (VdUtil.isNotEmpty(mbrSearch.getMemberId())) {
                MemberMstDto mbrMstDto = memberMstService.getMemberMstWec(mbrSearch);
                if (VdUtil.isEmpty(mbrMstDto)) {
                    MemberPidSearchDto memberPidSearch = new MemberPidSearchDto();
                    memberPidSearch.setOrgCd(mbrSearch.getOrgCd());
                    memberPidSearch.setPid(mbrSearch.getMemberId());
                    TMemberPid mbrPidEty = memberPidService.getMemberPid(memberPidSearch);
                    if(VdUtil.isNotEmpty(mbrPidEty)){
                        memberNo = mbrMstDto.getMemberNo();
                    }
                }else{
                    memberNo = mbrMstDto.getMemberNo();
                }
            }
        }

        return memberNo;
    }
}
