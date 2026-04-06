package com.albeepoint.core.biz.point.service;

import com.albeepoint.core.biz.cont.models.EnumCont;
import com.albeepoint.core.biz.cont.models.dto.ContRealDto;
import com.albeepoint.core.biz.cont.models.dto.ContSearch;
import com.albeepoint.core.biz.cont.models.entity.ContMstEntity;
import com.albeepoint.core.biz.cont.service.ContMstService;
import com.albeepoint.core.biz.org.models.EnumOrg;
import com.albeepoint.core.biz.point.models.dto.*;
import com.albeepoint.core.biz.point.models.entity.MemberPtMstEntity;
import com.albeepoint.core.biz.point.models.entity.TrHistDetailEntity;
import com.albeepoint.core.biz.stat.models.entity.ContRecordEntity;
import com.albeepoint.core.biz.stat.service.ContRecordService;
import com.albeepoint.core.biz.point.models.dto.*;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.albeepoint.core.common.models.ErrorCode.*;


@RequiredArgsConstructor
@Service
@Transactional
public class PtIsuUtilService {
    @Autowired
    ContMstService contService;
    @Autowired
    ContRecordService contRecService;
    @Autowired
    TrHistDetailService thdService;
    @Autowired
    PtUtilService ptUtilService;
    @Autowired
    MemberPtMstService mbrPtService;

    public static final Logger log = LogManager.getLogger(PtIsuUtilService.class);

    /*
        적립, 발급에 필요한 기본사항 체크 및 기본정보 조회
     */
    public BasePtReqDto getPtReqContInfo(BasePtReqDto req){
        VdUtil.emptyEc(req.getContNo(), BIZ_ERR_001005, "계약번호 필수 입력");

        // 회원정보 조회
        req.setMember(ptUtilService.checkMember(req));

        // 기관코드 미입력시 회원 기관코드로 대체
        if(VdUtil.isEmpty(req.getOrgCd())){
            req.setOrgCd(req.getMember().getOrgCd());
        }

        /// 기관 정보 조회 및 적립가능 여부 체크
        req.setOrg(ptUtilService.checkOrg(req));
        if(VdUtil.isNotEmpty(req.getOrg())) {
            VdUtil.ec(VdUtil.isNotEqual(req.getOrg().getSaveCanYn(), "Y"), BIZ_ERR_001055, "적립 불가 기관");
        }

        /// 지점  정보 조회 및 적립가능 여부 체크
        req.setBrch(ptUtilService.checkBrch(req));
        if(VdUtil.isNotEmpty(req.getBrch())) {
            VdUtil.ec(VdUtil.isNotEqual(req.getBrch().getSaveCanYn(), "Y"), BIZ_ERR_001056, "적립 불가 지점");
        }

        Long brchNo = VdUtil.isNotEmpty(req.getBrch()) ? req.getBrch().getBrchNo() : null;

        req.setCont(contService.getContMstWec(new ContSearch(req.getContNo())));

        /// 서브계약 체크하여 계약정보 설정. 적립 및 적립취소 모두 계약의 적립기간내에서 가능함
        ContRealDto contRealDto = ptUtilService.getContForTr(req.getCont(), req.getOrg().getOrgNo(), brchNo);
        VdUtil.ec(!DateUtil.isBetween(contRealDto.getIsuStartDt(), contRealDto.getIsuEndDt())
                , BIZ_ERR_001013, "적립기간 아님");

        // 계약
        req.setContReal(contRealDto);

        // 회원포인트마스터 조회
        if(VdUtil.isNotEmpty(req.getContNo())) {
            MemberPtMstEntity mbrPtEty = mbrPtService.getMemberPtMst(new MemberPtSearch(req));
            if (VdUtil.isNotEmpty(mbrPtEty)) {
                req.setMemberPt(mbrPtEty);
                req.setMemberPtNo(req.getMemberPt().getMemberPtNo());
            }
        }

        return req;
    }



    /*
        적립 제한 체크
            총 적립 제한 : 계약별, 기관별, 지점별 적립 제한 체크
            건별 적립 제한 : 회원의 기간별, 1회 발행시 적립 제한 체크
     */
    public void checkIsuLimit(IsuReqDto req){
        // 계약전체 적립제한
        if(VdUtil.isNotEmpty(req.getCont().getTotalIsuAbleCnt()) || VdUtil.isNotEmpty(req.getCont().getTotalIsuAblePt())){
            // 현재까지 계약적립 내역 + 적립하려는 내역이 계약전체 적립제한 초과시 제한
            ContRecordEntity contRecord = contRecService.getContRecordCont(req.getContNo());
            if(VdUtil.isNotEmpty(contRecord)){
                VdUtil.ec(VdUtil.isNotEmpty(req.getContReal().getTotalIsuAbleCnt())
                        && ((contRecord.getTotalIsuCnt() + 1) > req.getCont().getTotalIsuAbleCnt())
                        , BIZ_ERR_001089, "계약 총 적립 가능 횟수 초과\"");

                VdUtil.ec(VdUtil.isNotEmpty(req.getContReal().getTotalIsuAblePt())
                        && ((contRecord.getTotalIsuPt() + req.getIsuReqPt()) > req.getCont().getTotalIsuAblePt())
                        , BIZ_ERR_001090, "계약 총 적립 가능 포인트 초과");
            }
        }

        // 기관 적립제한
        if(VdUtil.isEqual(req.getContReal().getLocalGbCd(), EnumOrg.LocalGb.ORG)){
            ContRecordEntity contRecord = contRecService.getContRecordContOrg(req.getContNo(), req.getOrgNo());
            if(VdUtil.isNotEmpty(contRecord)){
                VdUtil.ec(VdUtil.isNotEmpty(req.getContReal().getTotalIsuAbleCnt())
                        && ((contRecord.getTotalIsuCnt() + 1) > req.getContReal().getTotalIsuAbleCnt())
                        , BIZ_ERR_001091, "계약기관 총 적립 가능 횟수 초과");

                VdUtil.ec(VdUtil.isNotEmpty(req.getContReal().getTotalIsuAblePt())
                        && ((contRecord.getTotalIsuPt() + req.getIsuReqPt()) > req.getContReal().getTotalIsuAblePt())
                        , BIZ_ERR_001092, "계약기관 총 적립 가능 포인트 초과");
            }
        }

        // 지점 적립제한
        if(VdUtil.isEqual(req.getContReal().getLocalGbCd(), EnumOrg.LocalGb.BRCH)){
            ContRecordEntity contRecord = contRecService.getContRecordContBrch(req.getContNo(), req.getOrgNo(), req.getBrchNo());
            if(VdUtil.isNotEmpty(contRecord)){
                VdUtil.ec(VdUtil.isNotEmpty(req.getContReal().getTotalIsuAbleCnt())
                        && ((contRecord.getTotalIsuCnt() + 1) > req.getContReal().getTotalIsuAbleCnt())
                        , BIZ_ERR_001093, "계약지점 총 적립 가능 횟수 초과");

                VdUtil.ec(VdUtil.isNotEmpty(req.getContReal().getTotalIsuAblePt())
                        && ((contRecord.getTotalIsuPt() + req.getIsuReqPt()) > req.getContReal().getTotalIsuAblePt())
                        , BIZ_ERR_001094, "계약지점 총 적립 가능 포인트 초과");
            }
        }

        // 고정적립인데 적립요청포인트 미입력시에는 계약상의 고정포인트로 설정
        if(VdUtil.isEqual(req.getCont().getOnceIsuLimitTypeCd(), EnumCont.OnceIsuLimitType.FIX)
            && VdUtil.isEmpty(req.getIsuReqPt())) {
            req.setIsuReqPt(req.getCont().getOnceIsuFixPt());
        }

        // 1회 적립제한
        if(VdUtil.isNotEqual(req.getCont().getOnceIsuLimitTypeCd(), EnumCont.OnceIsuLimitType.NONE)){
            VdUtil.ec(VdUtil.isEqual(req.getCont().getOnceIsuLimitTypeCd(), EnumCont.OnceIsuLimitType.FIX)
                            && VdUtil.isNotEmpty(req.getCont().getOnceIsuFixPt()) && VdUtil.isNotEqual(req.getCont().getOnceIsuFixPt(), req.getIsuReqPt())
                    , BIZ_ERR_001037, "1회 발행 포인트 불일치(FIX)");

            VdUtil.ec(VdUtil.isEqual(req.getCont().getOnceIsuLimitTypeCd(), EnumCont.OnceIsuLimitType.MIN)
                            && req.getCont().getOnceIsuMinPt() < req.getIsuReqPt()
                    , BIZ_ERR_001038, "1회 최소 발행 포인트 미만(MIN)");

            VdUtil.ec(VdUtil.isEqual(req.getCont().getOnceIsuLimitTypeCd(), EnumCont.OnceIsuLimitType.MAX)
                            && req.getCont().getOnceIsuMaxPt() > req.getIsuReqPt()
                    , BIZ_ERR_001039, "1회 최소 발행 포인트 초과(MAX)");

            VdUtil.ec(VdUtil.isEqual(req.getCont().getOnceIsuLimitTypeCd(), EnumCont.OnceIsuLimitType.BOTH)
                            && (req.getCont().getOnceIsuMinPt() < req.getIsuReqPt()
                            || req.getCont().getOnceIsuMaxPt() > req.getIsuReqPt())
                    , BIZ_ERR_001040, "1회 최소/최대 발행 포인트 미만 또는 초과(BOTH)");

            VdUtil.ec(VdUtil.isEqual(req.getCont().getOnceIsuLimitTypeCd(), EnumCont.OnceIsuLimitType.RATE)
                            && VdUtil.isEmpty(req.getPurchaseAmt())
                    , BIZ_ERR_001067, "구매금액 필수 입력");
        }

        if (VdUtil.isNotEqual(req.getCont().getIsuPeriodLimitTypeCd(), EnumCont.IsuPeriodLimitType.NONE)) {
            String isuStartDay = DateUtil.getFirstDayString(req.getCont().getIsuPeriodLimitTypeCd().toString(), DateUtil.getTodayString());
            String isuEndDay = DateUtil.getLastDayString(req.getCont().getIsuPeriodLimitTypeCd().toString(), DateUtil.getTodayString());

            PtSearch search = new PtSearch();
            search.setMemberNo(req.getMemberNo());
            search.setContNo(req.getContNo());
            search.setStartDt(DateUtil.convStringToTimestamp(isuStartDay));
            search.setEndDt(DateUtil.convStringToTimestamp(isuEndDay));

            TrHistDetailSumDto thdSum = thdService.getTrHistDetailIsuSum(search);
            VdUtil.ec(req.getCont().getIsuPeriodMaxCnt() > thdSum.getNetIsuCntSum(), BIZ_ERR_001035, "기간별 발행 가능 횟수 초과");
            VdUtil.ec(req.getCont().getIsuPeriodMaxPt() > thdSum.getNetIsuPtSum(), BIZ_ERR_001036, "기간별 발행 가능 포인트 초과");
        }
    }



    /*
        계약조건 및 입려값을 고려한 발행포인트 계산
     */
    public Long calcIsuPt(IsuReqDto req){
        ContRealDto contReal = req.getContReal();
        Long isuReqPt = req.getIsuReqPt();
        Long purchaseAmt = req.getPurchaseAmt();

        Long isuPt = 0L;

        // 계약정보상의 계약기간 여부 체크
        VdUtil.ec(Timestamp.valueOf(LocalDateTime.now()).before(contReal.getStartDt())
                    || Timestamp.valueOf(LocalDateTime.now()).after(contReal.getEndDt())
                , BIZ_ERR_001013, "계약 기간 아님");

        // 계약정보상의 사용가능구간 여부 체크
        VdUtil.ec(Timestamp.valueOf(LocalDateTime.now()).before(contReal.getIsuStartDt())
                || Timestamp.valueOf(LocalDateTime.now()).after(contReal.getIsuEndDt())
                , BIZ_ERR_001014, "적립 기간 아님");
        
        if(VdUtil.isNotEqual(contReal.getOnceIsuLimitTypeCd(), EnumCont.OnceIsuLimitType.FIX)
            && VdUtil.isNotEqual(contReal.getOnceIsuLimitTypeCd(), EnumCont.OnceIsuLimitType.RATE)){
            VdUtil.emptyEc(isuReqPt, BIZ_ERR_001006, "적립 포인트 필수 입력");
        }

        if(VdUtil.isEqual(contReal.getOnceIsuLimitTypeCd(), EnumCont.OnceIsuLimitType.NONE)){
            isuPt = isuReqPt;
        }else if(VdUtil.isEqual(contReal.getOnceIsuLimitTypeCd(), EnumCont.OnceIsuLimitType.FIX)){
            if(VdUtil.isNotEmpty(isuReqPt)) {
                VdUtil.notEqualEc(isuReqPt, contReal.getOnceIsuFixPt(), BIZ_ERR_001037, "1회 발행 포인트 불일치(FIX)");
            }
            isuPt = contReal.getOnceIsuFixPt();
        }else if(VdUtil.isEqual(contReal.getOnceIsuLimitTypeCd(), EnumCont.OnceIsuLimitType.MIN)){
            VdUtil.ec(isuReqPt < contReal.getOnceIsuMinPt(), BIZ_ERR_001063, "발행요청포인트가 1회 최소발행포인트 보다 작음");
            isuPt = isuReqPt;
        }else if(VdUtil.isEqual(contReal.getOnceIsuLimitTypeCd(), EnumCont.OnceIsuLimitType.MAX)){
            VdUtil.ec(isuReqPt > contReal.getOnceIsuMaxPt(), BIZ_ERR_001065, "발행요청포인트가 1회 최대발행포인트 보다 큼");
            isuPt = isuReqPt;
        }else if(VdUtil.isEqual(contReal.getOnceIsuLimitTypeCd(), EnumCont.OnceIsuLimitType.BOTH)){
            VdUtil.ec(isuReqPt < contReal.getOnceIsuMinPt(), BIZ_ERR_001063, "발행요청포인트가 1회 최소발행포인트 보다 작음");
            VdUtil.ec(isuReqPt > contReal.getOnceIsuMaxPt(), BIZ_ERR_001065, "발행요청포인트가 1회 최대발행포인트 보다 큼");
            isuPt = isuReqPt;
        }else if(VdUtil.isEqual(contReal.getOnceIsuLimitTypeCd(), EnumCont.OnceIsuLimitType.RATE)){
            VdUtil.emptyEc(purchaseAmt, BIZ_ERR_001067, "구매금액 필수 입력");
            isuPt = Math.round(purchaseAmt * contReal.getOnceIsuPurchaseRate());
        }

        return isuPt;
    }

    /*
        포인트 발급시, 포인트 사용시작일/종료일, 사용취소시작일/종료일 구하기
     */
    public Map<String, Timestamp> getUsePeriod(BasePtReqDto req, LocalDate baseDd){
        Map<String, Timestamp> usePeriod = new HashMap<>();
        Timestamp baseTs = Timestamp.valueOf(LocalDateTime.now());

        Timestamp useStartTs = null;
        Timestamp useEndTs = null;
        Timestamp expTs = null;

        /*
           사용시작일 설정. NONE 또는 FIX_DAY 는 계약정보대로 설정. 나머지는 오늘(포인트 적립일)을 기준으로 기간 계산하여 설정
         */
        if(VdUtil.isEqual(req.getCont().getUseStartDtCalcTypeCd(), EnumCont.UseStartDtCalcType.NONE)) {
            // 발급일시가 계약정보의 사용시작일시보다 이전이면 사용시작일시는 계약정보에 따름
            useStartTs = baseTs.before(req.getCont().getUseStartDt()) ? req.getCont().getUseStartDt() : baseTs;
        }else{
            // 발급일 + 기간 : 사용시작일로부터 기간의 마지막날. 예 : 발급일이 20230501 이고 AvpTypeCd 가 M, AvpVal 이 2 이면 사용시작일은 20230630 + 1일인 20230701.
            Timestamp tmp = DateUtil.getDDay(req.getCont().getUseStartDtCalcTypeCd().toString(), baseDd, req.getCont().getUseStartDtCalcVal());
            // 계산된 날짜의 다음날부터 사용기능함.
            useStartTs = Timestamp.valueOf(tmp.toLocalDateTime().plusDays(1));
        }

        if(useStartTs.after(req.getCont().getUseEndDt())){
            useStartTs = req.getCont().getUseEndDt();
        }

        /*
            사용종료일 설정. NONE 또는 FIX_DAY 는 계약정보대로 설정. 나머지는 사용시작일을 기준으로 기간 계산하여 설정
         */
        if(VdUtil.isEqual(req.getCont().getUsePeriodCalcTypeCd(), EnumCont.UsePeriodCalcType.NONE)) {
            useEndTs = req.getCont().getUseEndDt();
        }else {
            // 발급일 + 기간 : 사용시작일로부터 기간의 마지막날. 예 : 사용시작일이 20230501 이고 AvpTypeCd 가 M, AvpVal 이 2 이면 사용종료일은 20230630.
            useEndTs = DateUtil.getDDay(req.getCont().getUsePeriodCalcTypeCd().toString(), useStartTs.toLocalDateTime().toLocalDate(), req.getCont().getUsePeriodCalcVal());
        }

        if(useEndTs.after(req.getCont().getUseEndDt())){
            useEndTs = req.getCont().getUseEndDt();
        }

        // 소멸예정일 : 사용종료일(=사용가능마지막날 + 1일)에 포인트 소멸
        expTs = Timestamp.valueOf(useEndTs.toLocalDateTime().plusNanos(1));

        usePeriod.put("useStartTs", useStartTs);
        usePeriod.put("useEndTs", useEndTs);
        usePeriod.put("expTs", expTs);

        return usePeriod;
    }

}
