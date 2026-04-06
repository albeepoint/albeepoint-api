package com.albeepoint.core.biz.point.service;

import com.albeepoint.core.biz.cont.models.EnumCont;
import com.albeepoint.core.biz.point.models.EnumPoint;
import com.albeepoint.core.biz.point.models.dto.*;
import com.albeepoint.core.biz.point.models.entity.MemberPtMstEntity;
import com.albeepoint.core.biz.point.models.entity.PtMstEntity;
import com.albeepoint.core.biz.point.models.entity.TrHistDetailEntity;
import com.albeepoint.core.biz.point.models.entity.TrHistEntity;
import com.albeepoint.core.biz.point.models.ivo.request.IsuCancelReqIVo;
import com.albeepoint.core.biz.point.models.ivo.response.IsuCancelResultResIVo;
import com.albeepoint.core.biz.point.models.ivo.response.IsuResultResIVo;
import com.albeepoint.core.biz.point.models.ivo.response.MemberPtResIVo;
import com.albeepoint.core.common.exception.AlbeepointException;
import com.albeepoint.core.common.util.CommUtil;
import com.albeepoint.core.common.util.DateUtil;
import com.albeepoint.core.common.util.StrUtil;
import com.albeepoint.core.common.util.VdUtil;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.albeepoint.core.biz.cont.models.EnumCont.PtType.MILEAGE;
import static com.albeepoint.core.biz.point.models.EnumPoint.TrGb.S001;
import static com.albeepoint.core.biz.point.models.EnumPoint.TrGb.SC01;
import static com.albeepoint.core.biz.point.models.EnumPoint.TrMethodGb.ONLINE;
import static com.albeepoint.core.common.models.ErrorCode.*;


@RequiredArgsConstructor
@Service
@Transactional
public class PtIsuCancelService {
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

    public static final Logger log = LogManager.getLogger(PtIsuCancelService.class);

    /*
        적립 취소
     */
    public IsuCancelResultResIVo issueCancel(IsuCancelReqIVo reqVo){

        // 요청정보 설정 (필수 입력 값 및 적립/계약기간 체크 포함)
        IsuCancelReqDto req = reqVo.getIsuCancelReqDto();
        Long inputIsuCancelReqPt = req.getIsuCancelReqPt(); // 적립취소요청한 포인트

        req.setTrGbCd(SC01);
        BasePtReqDto basePtReqDto = ptIsuUtilService.getPtReqContInfo(req);
        CommUtil.objectCopy(basePtReqDto, req);

        if(StrUtil.enumEquals(req.getPtTypeCd(), MILEAGE)){
            req = issueCancelMileage(req);
        }else{
            req = issueCancelCoupon(req);
        }

        // 거래이력 인서트(거래이력상세 인서트 포함)
        Long trSno = setIsuCancelTrHist(req);

        IsuCancelResultResIVo resVo = new IsuCancelResultResIVo();
        resVo.setIsuCancelReqPt(VdUtil.isNotEmpty(inputIsuCancelReqPt) ? inputIsuCancelReqPt : resVo.getIsuCancelReqPt());
        resVo.setIsuCanceledPt(req.getIsuCancelReqPt());
        resVo.setAblePt(ptService.getTotalBalPtMstList(req.getMemberNo(), req.getContNo()));
        resVo.setTrSno(trSno);
        // contIsuService.minusContIsu(req.getContNo(), req.getIsuCancelReqPt(), 1L); // 총발행내역 저장
        // MemberPtResVo resVo = isuCancelMileage(req);

        return resVo;
    }

    /*
        마일리지형 적립 취소
         원거래번호 불필요 무시함. 잔여포인트 한도내에서 적립취소 요청 포인트만큼 취소 처리함
     */
    public IsuCancelReqDto issueCancelMileage(IsuCancelReqDto req){
        VdUtil.ec(VdUtil.isEmpty(req.getContNo()) || VdUtil.isEmpty(req.getIsuCancelReqPt())
                , BIZ_ERR_001135, "마일리지형 계약의 적립 취소시에는 계약번호와 적립 취소 요청포인트 필수입력");

        Long contNo = req.getContNo();
        Long isuCancelReqPt = req.getIsuCancelReqPt();

        // 포인트마스터의 유효한(미소멸) 잔여포인트 합계
        long contBefBalPt = mbrPtService.getMemberPtMstWec(new MemberPtSearch(req.getMemberPtNo())).getBalPt();

        VdUtil.ec(isuCancelReqPt > contBefBalPt, BIZ_ERR_001123 , "적립취소 가능 잔여 포인트 부족");

        log.info("issue cancel request pt ==> " + isuCancelReqPt);

        // 회원포인트마스터 업데이트
        MemberPtDto mbrPtDto = new MemberPtDto(req.getMemberPtNo());
        mbrPtDto.setIsuCancelPt(isuCancelReqPt);
        mbrPtService.updateIsuCancelMemberPtMst(mbrPtDto);

        // 포인트마스터 업데이트
        List<PtReqDto> isuCancelTargetList = new ArrayList<>();
        PtSearch ptSearch = new PtSearch(req.getMemberNo(), contNo);
        ptSearch.setReqPt(isuCancelReqPt);
        List<PtMstTargetDto> targetList = ptService.getBalPtTargetListByReqPt(ptSearch);
        List<PtReqDto> cancelList = new ArrayList<>();
        long remainingContBefBalPt = contBefBalPt;
        for(PtMstTargetDto icTgt : targetList){
            PtReqDto ptReqDto = new PtReqDto(req, req.getContReal(), icTgt.getPtNo(), icTgt.getReqPt());
            ptReqDto.setContNo(icTgt.getContNo());
            ptReqDto.setMemberPtNo(icTgt.getMemberPtNo());
            ptReqDto.setTrGbCd(SC01);
            ptReqDto.setBalPt(ptService.getPtMstWec(new PtSearch(icTgt.getPtNo())).getBalPt());
            ptReqDto.setContBalPt(remainingContBefBalPt);
            ptReqDto.setReqPt(icTgt.getReqPt());
            ptReqDto.setOrglTrSno(req.getOrglTrSno());
            isuCancelTargetList.add(ptReqDto);
            remainingContBefBalPt = remainingContBefBalPt - icTgt.getReqPt();
        }
        ptService.isuCancelMultiPtMst(isuCancelTargetList);

        // 포인트마스터에 업데이트한 총 적립취소 포인트 합계
        Long totalIsuCancelPt = isuCancelTargetList.stream().collect(Collectors.summingLong(PtReqDto::getIsuCancelReqPt));
        VdUtil.notEqualEc(req.getIsuCancelReqPt(), totalIsuCancelPt, BIZ_ERR_001128
                , "적립취소포인트 합계 오류. [" + req.getIsuCancelReqPt() + "], [" + totalIsuCancelPt + "]");

        MemberPtMstEntity mbrPtEty = mbrPtService.getMemberPtMst(new MemberPtSearch(req.getMemberNo(), req.getContNo()));
        log.info("After MemberPtMst ==> " + mbrPtEty);

        req.setContBefBalPt(contBefBalPt);
        req.setIsuCancelPtList(isuCancelTargetList);
        return req;
    }

    /*
        쿠폰형 적립 취소
         원거래번호만 입력 : 원거래번호로 적립된 내역 중 잔여포인트만큼 적립취소. PT_MST 에서 해당 날짜 잔여포인트내에서 적립취소
         원거래번호 + 적립취소요청포인트 입력 : 원거래번호로 적립된 내역 중 잔여포인트한도내에서 적립취소요청포인트만큼 적립취소. PT_MST 에서 해당 날짜 잔여포인트내에서 적립취소
         적립취소요청포인트만 입력 : 먼저 적립한 건부터 적립취소
     */
    public IsuCancelReqDto issueCancelCoupon(IsuCancelReqDto req){
        VdUtil.ec(VdUtil.isEmpty(req.getOrglTrSno()) && VdUtil.isEmpty(req.getIsuCancelReqPt())
                , BIZ_ERR_001041, "적립취소 대상 필수 입력");

        TrHistDetailEntity orglThdEty = null;
        if(VdUtil.isNotEmpty(req.getContNo()) && VdUtil.isNotEmpty(req.getOrglTrSno())){
            // 계약번호, 원거래번호 모두 입력
            orglThdEty = thdService.getTrHistDetailWec(new TrHistSearch(req.getMemberNo(), req.getOrglTrSno()));
            VdUtil.emptyEc(orglThdEty, BIZ_ERR_001115, "취소대상 원거래 없음");
            VdUtil.notEqualEc(orglThdEty.getContNo(), req.getContNo(), BIZ_ERR_001134, "계약번호와 원거래 불일치");
        }else if(VdUtil.isEmpty(req.getContNo()) && VdUtil.isNotEmpty(req.getOrglTrSno())){
            // 계약번호 미입력, 원거래번호 입력
            orglThdEty = thdService.getTrHistDetailWec(new TrHistSearch(req.getMemberNo(), req.getOrglTrSno()));
            VdUtil.emptyEc(orglThdEty, BIZ_ERR_001115, "취소대상 원거래 없음");
            req.setContNo(orglThdEty.getContNo());
        }else if(VdUtil.isNotEmpty(req.getContNo()) && VdUtil.isEmpty(req.getOrglTrSno())){
            // 계약번호 입력, 원거래번호 미입력
            TrHistSearch thdSearch = new TrHistSearch();
            thdSearch.setMemberNo(req.getMemberNo());
            thdSearch.setTrGbCd(S001);
            orglThdEty = thdService.getLastOneTrHistDetail(thdSearch);
            VdUtil.emptyEc(orglThdEty, BIZ_ERR_001115, "취소대상 원거래 없음");
            VdUtil.notEqualEc(orglThdEty.getContNo(), req.getContNo(), BIZ_ERR_001134, "계약번호와 원거래 불일치");
            req.setOrglTrSno(orglThdEty.getTrSno());
        }else{
            new AlbeepointException(BIZ_ERR_001133, "계약번호 또는 원거래번호 필수 입력");
        }

        PtMstEntity orglPtEty = ptService.getPtMst(new PtSearch(orglThdEty.getPtNo()));
        VdUtil.emptyEc(orglPtEty.getNetBalPt(), BIZ_ERR_001123 , "해당 일자 적립포인트 중 미사용/미소멸 잔여포인트 없음");
        req.setPtNo(orglPtEty.getPtNo());

        Long contNo = req.getContNo();
        Long isuCancelReqPt = req.getIsuCancelReqPt();

        // 포인트마스터의 유효한(미소멸) 잔여포인트 합계
        long contBefBalPt = mbrPtService.getMemberPtMstWec(new MemberPtSearch(orglThdEty.getMemberPtNo())).getBalPt();

        if(VdUtil.isEmpty(isuCancelReqPt)){
            // 원거래번호 만 입력된 경우
            req.setIsuCancelReqPt(orglThdEty.getTrPt() > orglPtEty.getNetBalPt() ? orglPtEty.getNetBalPt() : orglThdEty.getTrPt());
            isuCancelReqPt = req.getIsuCancelReqPt();
        }else{
            // 원거래번호 + 적립취소요청포인트 입력된 경우
            VdUtil.ec(isuCancelReqPt > orglThdEty.getTrPt(), BIZ_ERR_001117, "취소 요청 포인트가 원거래 포인트보다 클 수 없음");
            VdUtil.ec(isuCancelReqPt > orglPtEty.getNetBalPt(), BIZ_ERR_001123 , "적립취소 가능 잔여 포인트 부족");
        }

        log.info("issue cancel request pt ==> " + isuCancelReqPt);

        // 회원포인트마스터 업데이트
        MemberPtDto mbrPtDto = new MemberPtDto(req.getMemberPtNo());
        mbrPtDto.setIsuCancelPt(isuCancelReqPt);
        mbrPtService.updateIsuCancelMemberPtMst(mbrPtDto);

        // 포인트마스터 업데이트
        List<PtReqDto> isuCancelTargetList = new ArrayList<>();
        if(VdUtil.isNotEmpty(req.getOrglTrSno())){
            // 원거래번호 입력시에는 해당 거래(즉, 1개의 PT_MST 적립건)만 업데이트
            PtReqDto ptReqDto = new PtReqDto(req, req.getContReal(), req.getPtNo(), isuCancelReqPt);
            ptReqDto.setMemberPtNo(req.getMemberPtNo());
            ptReqDto.setContNo(req.getContNo());
            ptReqDto.setTrGbCd(SC01);
            ptReqDto.setBalPt(ptService.getPtMstWec(new PtSearch(req.getPtNo())).getBalPt());
            ptReqDto.setContBalPt(contBefBalPt);
            ptReqDto.setReqPt(isuCancelReqPt);
            ptReqDto.setOrglTrSno(req.getOrglTrSno());
            isuCancelTargetList.add(ptReqDto);
            ptService.isuCancelPtMst(ptReqDto);
        }else{
            // 원거래번호 미입력시에는 원하는 포인트만큼 적립 취소 할 수 있으므로 다건의 PT_MST가 업데이트 할 수 있음
            PtSearch ptSearch = new PtSearch(req.getMemberNo(), contNo);
            ptSearch.setReqPt(isuCancelReqPt);
            List<PtMstTargetDto> targetList = ptService.getBalPtTargetListByReqPt(ptSearch);
            List<PtReqDto> cancelList = new ArrayList<>();
            long remainingContBefBalPt = contBefBalPt;
            for(PtMstTargetDto icTgt : targetList){
                PtReqDto ptReqDto = new PtReqDto(req, req.getContReal(), icTgt.getPtNo(), icTgt.getReqPt());
                ptReqDto.setContNo(icTgt.getContNo());
                ptReqDto.setMemberPtNo(icTgt.getMemberPtNo());
                ptReqDto.setTrGbCd(SC01);
                ptReqDto.setBalPt(ptService.getPtMstWec(new PtSearch(req.getPtNo())).getBalPt());
                ptReqDto.setContBalPt(remainingContBefBalPt);
                ptReqDto.setReqPt(icTgt.getReqPt());
                ptReqDto.setOrglTrSno(req.getOrglTrSno());
                cancelList.add(ptReqDto);
                remainingContBefBalPt = remainingContBefBalPt - icTgt.getReqPt();
            }
            ptService.isuCancelMultiPtMst(cancelList);
        }

        // 포인트마스터에 업데이트한 총 적립취소 포인트 합계
        Long totalIsuCancelPt = isuCancelTargetList.stream().collect(Collectors.summingLong(PtReqDto::getIsuCancelReqPt));
        VdUtil.notEqualEc(req.getIsuCancelReqPt(), totalIsuCancelPt, BIZ_ERR_001128
                , "적립취소포인트 합계 오류. [" + req.getIsuCancelReqPt() + "], [" + totalIsuCancelPt + "]");

        MemberPtMstEntity mbrPtEty = mbrPtService.getMemberPtMst(new MemberPtSearch(req.getMemberNo(), req.getContNo()));
        log.info("After MemberPtMst ==> " + mbrPtEty);

        req.setContBefBalPt(contBefBalPt);
        req.setIsuCancelPtList(isuCancelTargetList);
        return req;
    }

    private Long setIsuCancelTrHist(IsuCancelReqDto req){
        Long orgNo = req.getOrgNo();
        Long brchNo = VdUtil.isNotEmpty(req.getBrch()) ? req.getBrch().getBrchNo() : null;
        long isuCancelPt = req.getIsuCancelReqPt();

        TrHistEntity trHist = new TrHistEntity();
        long newTrHistSno = thService.getNewTrSno();
        trHist.setTrSno(newTrHistSno);
        trHist.setMemberNo(req.getMemberNo());
        trHist.setTrGbCd(SC01);
        trHist.setTrMethodGbCd(ONLINE);
        trHist.setOrgNo(orgNo);
        trHist.setBrchNo(brchNo);
        trHist.setTrPt(req.getIsuCancelReqPt());
        trHist.setTrDy(DateUtil.getNowTimeZoneDayString());
        trHist.setBefPt(req.getContBefBalPt());
        trHist.setAfrPt(req.getContBefBalPt() - isuCancelPt);
        trHist.setOrglTrSno(req.getOrglTrSno() != null ? req.getOrglTrSno() : null);
        thService.regTrHist(trHist);

        // TR_HIST_DETAIL 인서트
        TrHistSearch trHistSearch = new TrHistSearch();
        trHistSearch.setMemberNo(trHist.getMemberNo());
        trHistSearch.setTrSno(newTrHistSno);
        trHistSearch.setIsuCancelTargetList(req.getIsuCancelPtList());

        thdService.regIsuCancelMultiTrHistDetail(trHistSearch);

        trHist.setTrSno(newTrHistSno);

        // contIsuService.minusContIsu(req.getContNo(), accCancelPtSum, 1L); // 총발행내역 저장

        return trHist.getTrSno();
    }
}
