package com.albeepoint.core.biz.point.service;

import com.albeepoint.core.biz.cont.models.EnumCont;
import com.albeepoint.core.biz.point.models.EnumPoint;
import com.albeepoint.core.biz.point.models.dto.*;
import com.albeepoint.core.biz.point.models.ivo.request.IsuReqIVo;
import com.albeepoint.core.biz.point.models.ivo.response.IsuResultResIVo;
import com.albeepoint.core.biz.point.models.ivo.response.MemberPtResIVo;
import com.albeepoint.core.biz.stat.service.ContRecordService;
import com.albeepoint.core.biz.point.models.entity.MemberPtMstEntity;
import com.albeepoint.core.biz.point.models.entity.PtMstEntity;
import com.albeepoint.core.biz.point.models.entity.TrHistDetailEntity;
import com.albeepoint.core.biz.point.models.entity.TrHistEntity;
import com.albeepoint.core.common.util.CommUtil;
import com.albeepoint.core.common.util.DateUtil;
import com.albeepoint.core.common.util.VdUtil;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Map;

import static com.albeepoint.core.biz.point.models.EnumPoint.TrGb.S001;
import static com.albeepoint.core.biz.point.models.EnumPoint.TrMethodGb.ONLINE;


@RequiredArgsConstructor
@Service
@Transactional
public class PtIsuService {
    @Autowired
    PtMstService ptService;
    @Autowired
    MemberPtMstService mbrPtService;   
    @Autowired
    TrHistService thService;
    @Autowired
    TrHistDetailService thdService;
    @Autowired
    PtUtilService ptUtilService;
    @Autowired
    PtIsuUtilService ptIsuUtilService;
    @Autowired
    ContRecordService contRecService;

    public static final Logger log = LogManager.getLogger(PtIsuService.class);

    public IsuResultResIVo issue(IsuReqIVo reqVo) {
        IsuReqDto req = reqVo.getIsuReqDto();
        Long inputIsuReqPt = reqVo.getIsuReqPt();  // 적립요청포인트

        // 서브계약 고려한 실제 계약정보 설정(contReal), 요청정보 설정 (필수 입력 값 및 적립/계약기간 체크 포함)
        BasePtReqDto basePtReqDto = ptIsuUtilService.getPtReqContInfo(req);
        CommUtil.objectCopy(basePtReqDto, req);

        // 적립 제한 체크(총 적립제한, 건별 적립제한)
        ptIsuUtilService.checkIsuLimit(req);

        // 1차로 발행포인트 계산 및 설정. 포인트종류별로 더 상세하게 발행포인트 추가 계산 예정
        Long isuPt = ptIsuUtilService.calcIsuPt(req);
        req.setIsuReqPt(isuPt);

        // 사용기간 설정
        Map<String, Timestamp> usePeriod = ptIsuUtilService.getUsePeriod(req, LocalDate.now());
        req.setUseStartDt(usePeriod.get("useStartTs"));
        req.setUseEndDt(usePeriod.get("useEndTs"));
        req.setExpDt(usePeriod.get("expTs"));

        /// 회원포인트 마스터 저장
        long ptBefBalPt = 0;     // 거래전 포인트마스터 잔여포인트
        long contBefBalPt = 0;   // 거래전 회원포인트마스터 잔여포인트
        MemberPtMstEntity mbrPtEty = mbrPtService.getMemberPtMst(new MemberPtSearch(req));
        if(VdUtil.isNotEmpty(mbrPtEty)) {
            req.setMemberPtNo(mbrPtEty.getMemberPtNo());
            contBefBalPt = ptService.getTotalBalPtMstList(req.getMemberNo(), req.getContNo());
            // 마일리지 타입이면 일자별로 PT_MST 등록하므로 당일건 존재시 잔여포인트 설정
            if (VdUtil.isEqual(req.getCont().getPtTypeCd(), EnumCont.PtType.MILEAGE)) {
                PtMstEntity ptMst = ptService.getPtMst(new PtSearch(req.getMemberNo(), req.getContNo(), DateUtil.getNowTimeZoneDayString()));
                if (VdUtil.isNotEmpty(ptMst)) {
                    ptBefBalPt = ptMst.getBalPt();
                }
            }
        }

        mbrPtEty = mbrPtService.isuMemberPtMst(mbrPtEty, req);
        req.setMemberPtNo(mbrPtEty.getMemberPtNo());

        PtMstEntity ptEty = null;
        if(req.getCont().getPtTypeCd().equals(EnumCont.PtType.MILEAGE)){
            /// 포인트 마스터 저장
            // 마일리지형은 일자별로 포인트마스터 관리하므로 먼저 해당일자 존재 여부 체크
            PtSearch ptSearch = new PtSearch(req.getMemberNo(), req.getContNo());
            ptSearch.setIsuDy(DateUtil.getTodayString());
            ptEty = ptService.getPtMst(ptSearch);
            if(VdUtil.isEmpty(ptEty)) {
                ptBefBalPt = 0;
                ptEty = ptService.regPtMst(new PtMstEntity(req));
            }else{
                ptBefBalPt = ptEty.getBalPt();
                ptService.isuPtMst(new PtReqDto(ptEty.getPtNo(), req.getIsuReqPt()));
                ptEty = ptService.getPtMst(new PtSearch(ptEty.getPtNo()));
            }
        }else{
            /// 포인트 마스터 저장
            ptEty = ptService.regPtMst(new PtMstEntity(req));
        }
        req.setPtNo(ptEty.getPtNo());

        /// 거래내역 저장
        Long trSno = setIsuTrHist(req, contBefBalPt, ptBefBalPt);

        IsuResultResIVo resVo = new IsuResultResIVo();
        resVo.setIsuReqPt(VdUtil.isNotEmpty(inputIsuReqPt) ? inputIsuReqPt : resVo.getIsuReqPt());
        resVo.setIsudPt(req.getIsuReqPt());
        resVo.setAblePt(ptService.getTotalBalPtMstList(req.getMemberNo(), req.getContNo()));
        resVo.setTrSno(trSno);
        return resVo;
    }


    // 발급이력 저장
    private Long setIsuTrHist(IsuReqDto req, Long contBefBalPt, Long ptBefBalPt){
        Long contNo = req.getContNo();
        Long orgNo = req.getOrgNo();
        Long brchNo = VdUtil.isNotEmpty(req.getBrch()) ? req.getBrch().getBrchNo() : null;
        Long ptNo = req.getPtNo();
        long isuPt = req.getIsuReqPt();

        /// 거래내역 저장
        TrHistEntity trHist = new TrHistEntity();
        long newTrHistSno = thService.getNewTrSno();
        trHist.setTrSno(newTrHistSno);
        trHist.setMemberNo(req.getMemberNo());
        trHist.setTrGbCd(S001);
        trHist.setTrMethodGbCd(VdUtil.isNotEmpty(req.getIsuInfo()) ? EnumPoint.TrMethodGb.valueOf(req.getIsuInfo()) : ONLINE);
        trHist.setOrgNo(orgNo);
        trHist.setBrchNo(brchNo);
        trHist.setTrPt(isuPt);
        trHist.setTrDy(DateUtil.getNowTimeZoneDayString());
        trHist.setBefPt(contBefBalPt);
        trHist.setAfrPt(contBefBalPt + isuPt);
        trHist.setOrglTrSno(VdUtil.isNotEmpty(req.getOrglTrSno()) ? req.getOrglTrSno() : null);
        thService.regTrHist(trHist);

        TrHistDetailEntity thdEty = new TrHistDetailEntity();
        thdEty.setTrSno(trHist.getTrSno());
        thdEty.setTrSeq(1L);
        thdEty.setContNo(contNo);
        thdEty.setMemberNo(trHist.getMemberNo());
        thdEty.setTrGbCd(S001);
        thdEty.setTrMethodGbCd(VdUtil.isNotEmpty(req.getIsuInfo()) ? EnumPoint.TrMethodGb.valueOf(req.getIsuInfo()) : ONLINE);
        thdEty.setTrPt(isuPt);
        thdEty.setPtNo(req.getPtNo());
        thdEty.setMemberPtNo(req.getMemberPtNo());

        //  SQL 을 통해 PT_NO, BEF_BAL_PT, AFR_BAL_PT 추출하여 설정
        /*
        CalcBefAfrBalDto afrBalDto = ptService.getCalcBefAfrBalPt(S001, ptNo, isuPt);
        thdEty.setBefBalPt(afrBalDto.getBefBalPt());
        thdEty.setAfrBalPt(afrBalDto.getAfrBalPt());
         */
        thdEty.setBefBalPt(ptBefBalPt);
        thdEty.setAfrBalPt(ptBefBalPt + isuPt);
        thdEty.setContBefBalPt(contBefBalPt);
        thdEty.setContAfrBalPt(contBefBalPt + isuPt);
        thdEty.setOrglTrSno(VdUtil.isNotEmpty(req.getOrglTrSno()) ? req.getOrglTrSno() : null);
        thdService.regTrHistDetail(thdEty);

        contRecService.isuRecord(contNo, orgNo, brchNo, isuPt, 1L); // 총발행내역 저장
        return trHist.getTrSno();
    }

}
