package com.albee.albeepoint.api.point.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albee.albeepoint.api.contract.dto.ContSearchDto;
import com.albee.albeepoint.api.contract.service.ContMstService;
import com.albee.albeepoint.api.point.dto.BasePtReqDto; 

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class PtUseUtilService {
    @Autowired
    private PtMstMapper ptMapper;
    @Autowired
    private MemberPtMstService mbrPtService;
    @Autowired
    private ContOrgService contOrgService;
    @Autowired
    private TrHistService thService;
    @Autowired
    private TrHistDetailService thdService;
    @Autowired
    private PtUtilService ptUtilService;
    @Autowired
    private ContMstService contService; 

    /*
        사용, 사용에 필요한 기본사항 체크 및 기본정보 조회
            계약기간, 계약사용기간, 기관유효성, 지점유효성, 계약기관, 계약지점
     */
    public BasePtReqDto getPtReqUseInfo(BasePtReqDto req){
        // 계약정보 조회 및 사용 가능 기간 체크
        req.setCont(this.contService.getContMstWec(new ContSearchDto(req.getContNo())));

        // 계약마스터엔티티는 반드시 서브계약까지 감안한 contRealDto 를 통해 설정해야 함
        ContRealDto contRealDto = ptUtilService.getContForTr(req.getCont(), req.getOrgNo(), req.getBrchNo());

        req.setContReal(contRealDto);

        // 사용 및 사용취소시에 모두 사용기간이내이어야 함
        DateUtil.betweenTsEc(req.getCont().getUseStartDt(), req.getCont().getUseEndDt(), BIZ_ERR_001016, "사용기간 아님");

        // 기관정보 조회 및 사용가능 기관 체크
        req.setOrg(ptUtilService.checkOrg(req));
        ContOrgEntity contOrg = contOrgService.getContOrg(new ContOrgSearch(req.getContNo(), null, req.getOrg().getOrgNo()));
        VdUtil.ec(VdUtil.isEmpty(contOrg) || VdUtil.isNotEqual(contOrg.getContOrgStsCd(), EnumCont.ContOrgSts.NORMAL)
                , BIZ_ERR_001034, "계약 기관이 아님");
        VdUtil.notEqualEc(contOrg.getUseCanYn(), "Y", BIZ_ERR_001057, "사용 불가 기관");

        // 지점정보 조회 및 사용가능 지점 체크
        if(VdUtil.isEmpty(req.getBrch())) {
            req.setBrch(ptUtilService.checkBrch(req));
        }

        // 지점정보가 존재하는 경우 사용가능 여부 체크
        if(VdUtil.isNotEmpty(req.getBrch())) {
            if (VdUtil.isNotEqual(contOrg.getBrchPolicyTypeCd(), EnumOrg.BrchPolicyType.ALL)) {
                VdUtil.notEqualEc(req.getBrch().getSaveCanYn(), "Y", BIZ_ERR_001058, "사용 불가 지점");
            }
        }

        // 회원정보 조회
        // req.setMember(checkMember(req));

        return req;
    }


    /*
        계약별 계약조건 및 입력값을 고려한 사용 가능 포인트 계산
     */
    public Long calcPtAblePt(Long contBalPt, PtMstTargetDto ptTarget, MemberMstEntity member, Long useReqPt, Long purchaseAmt, Boolean isForUse){
        Long ablePt = 0L;
        ContRealDto contReal = ptTarget.getContReal();

        // 계약정보 상의 계약기간 여부 체크
        if(!DateUtil.isBetweenAp(contReal.getStartDt(), contReal.getEndDt())
            || !DateUtil.isBetweenAp(contReal.getUseStartDt(), contReal.getUseEndDt())){
            return 0L;
        }

        // 사용가능포인트 조회인 경우 사용요청포인트 미입력일 수 있으므로 일단 잔여포인트로 설정
        if(isForUse){
            VdUtil.ec(VdUtil.isEmpty(useReqPt), BIZ_ERR_001074, "사용 포인트 필수 입력");
        }else{
            if (VdUtil.isEmpty(useReqPt)) {
                useReqPt = ptTarget.getBalPt();
            }
        }

        // 사용요청포인트보다 잔여포인트가 작으면 사용요청포인트를 잔여포인트로 설정
        if(useReqPt > ptTarget.getBalPt()){
            useReqPt = ptTarget.getBalPt();
        }

        // 1회사용가능 포인트 체크
        if(isForUse) {
            // 1회사용제한 체크
            if (VdUtil.isEqual(contReal.getOnceUseLimitTypeCd(), EnumCont.OnceUseLimitType.NONE)) {
                ablePt = useReqPt;
            } else if (VdUtil.isEqual(contReal.getOnceUseLimitTypeCd(), EnumCont.OnceUseLimitType.FIX)) {
                VdUtil.emptyEc(contReal.getOnceUseFixPt(), BIZ_ERR_001068, "계약정보 1회 고정사용포인트 오류");
                if(useReqPt > contReal.getOnceUseFixPt()){
                    if(contBalPt >= contReal.getOnceUseFixPt()){
                        if(ptTarget.getBalPt() >= contReal.getOnceUseFixPt()){
                            // 계약의 잔여포인트가 고정사용포인트보다 크면 고정사용포인트만큼 사용
                            ablePt = contReal.getOnceUseFixPt();
                        }else{
                            ablePt = ptTarget.getBalPt();
                        }
                    }else{
                        ablePt = 0L;
                    }
                }else{
                    ablePt = 0L;  // 사용요청포인트가 고정사용포인트보다 작으므로 사용 불가
                }
                ablePt = contReal.getOnceUseFixPt(); // 입력한 사용요청포인트 관계없이 계약상 정의된 포인트로 설정
            } else if (VdUtil.isEqual(contReal.getOnceUseLimitTypeCd(), EnumCont.OnceUseLimitType.MIN)) {
                VdUtil.emptyEc(contReal.getOnceUseMinPt(), BIZ_ERR_001069, "계약정보 1회 최소사용포인트 오류");
                if(useReqPt < contReal.getOnceUseMinPt()){
                    log.info("사용요청포인트가 1회 최소사용포인트 보다 작아서 사용가능포인트 0 응답");
                    ablePt = 0L;
                }else {
                    ablePt = useReqPt;
                }
            } else if (VdUtil.isEqual(contReal.getOnceUseLimitTypeCd(), EnumCont.OnceUseLimitType.MAX)) {
                VdUtil.emptyEc(contReal.getOnceUseMaxPt(), BIZ_ERR_001071, "계약정보 1회 최대사용포인트 오류");
                VdUtil.ec(useReqPt > contReal.getOnceUseMaxPt(), BIZ_ERR_001072, "사용요청포인트가 1회 최대사용포인트 보다 큼");
                if(useReqPt > contReal.getOnceUseMaxPt()){
                    log.info("사용요청포인트가 1회 최대사용포인트 보다 커서 사용가능포인트 0 응답");
                    ablePt = 0L;
                }else {
                    ablePt = useReqPt;
                }
            } else if (VdUtil.isEqual(contReal.getOnceUseLimitTypeCd(), EnumCont.OnceUseLimitType.BOTH)) {
                VdUtil.emptyEc(contReal.getOnceUseMinPt(), BIZ_ERR_001069, "계약정보 1회 최소사용포인트 오류");
                VdUtil.emptyEc(contReal.getOnceUseMaxPt(), BIZ_ERR_001071, "계약정보 1회 최대사용포인트 오류");
                if((useReqPt < contReal.getOnceUseMinPt()) || (useReqPt > contReal.getOnceUseMaxPt())){
                    log.info("사용요청포인트가 1회최소사용포인트보다 작거나, 1회최대사용포인트보다 커서 사용가능포인트 0 응답");
                    ablePt = 0L;
                }else {
                    ablePt = useReqPt;
                }
            } else if (VdUtil.isEqual(contReal.getOnceUseLimitTypeCd(), EnumCont.OnceUseLimitType.RATE)) {
                VdUtil.emptyEc(contReal.getOnceUsePurchaseRate(), BIZ_ERR_001073, "계약정보 1회 구매금액발행비율 오류");
                log.info("구매금액 미입력으로 사용가능포인트 0 응답");
                // 사용요청면서 1회 사용제한조건이 구매금액비율인 경우, 구매금액 미입력이면 ==> 0으로 응답
                ablePt = VdUtil.isNotEmpty(purchaseAmt) ? Math.round(purchaseAmt * contReal.getOnceUsePurchaseRate()) : 0L;
            }
        }else{
            // 사용선조회이면서 1회 사용제한조건이 구매금액비율인 경우, 구매금액 미입력이면 ==> 잔여포인트로 응답
            if(VdUtil.isEqual(contReal.getOnceUseLimitTypeCd(), EnumCont.OnceUseLimitType.RATE)){
                ablePt = VdUtil.isNotEmpty(purchaseAmt) ? Math.round(purchaseAmt * contReal.getOnceUsePurchaseRate()) : ptTarget.getBalPt();
            }else{
                ablePt = useReqPt;
            }
        }

        // 기간별
        if(VdUtil.isNotEqual(contReal.getUsePeriodLimitTypeCd(), EnumCont.UsePeriodLimitType.NONE)){
            PtSearch ptSearch = new PtSearch(member.getMemberNo(), contReal.getContNo());
            String useStartDay = DateUtil.getFirstDayString(contReal.getUsePeriodLimitTypeCd().toString(), DateUtil.getTodayString());
            String useEndDay = DateUtil.getLastDayString(contReal.getUsePeriodLimitTypeCd().toString(), DateUtil.getTodayString());
            ptSearch.setStartDt(DateUtil.convStringToTimestampForStart(useStartDay));
            ptSearch.setEndDt(DateUtil.convStringToTimestampForEnd(useEndDay));

            TrHistDetailSumDto trSum = thdService.getTrHistDetailUseSum(ptSearch);

            // maxCnt 보다 지금 사용하려는 1건 포함한 전체 사용건수보다 크거나 같으면 사용횟수 초과로 사용 불가
            if(contReal.getUsePeriodMaxCnt() <= (trSum.getNetUseCntSum() + 1)){
                return 0L;
            }

            // maxPt 가 전체 사용포인트보다 크면 balPt와 비교하여 계산
            if(contReal.getUsePeriodMaxPt() > trSum.getNetUsePtSum()){
                long remainingMaxPt = contReal.getUsePeriodMaxPt() - trSum.getNetUsePtSum();
                ablePt = remainingMaxPt > ablePt ? ablePt : ablePt - remainingMaxPt;
            }else{
                return 0L;
            }
        }

        if(ablePt > useReqPt){
            ablePt = useReqPt;
        }

        if(ablePt > contBalPt){
            ablePt = contBalPt;
        }

        return ablePt;
    }

    // 회원 계약 사용가능 포인트 계산
    /*
        ContAblePtDto 를 리턴으로 수정
     */
    public Long calcMemberPtMstAblePt(ContRealDto contReal, Long contBalPt, Long memberNo, Long purchaseAmt){
        /*
            1. 계약정보 조회
            2. 회원포인트마스터 조회
                잔여포인트 0 이하이면 사용가능포인트 0
            3. 1회 사용 제한 체크
                잔여포인트가 1회 최저사용 포인트 미만시 사용가능포인트 0
            4. 계약정보 중 기간별 사용제한 존재시 체크
                기간별 사용제한만큼 이미 사용한 이력 존재하면 사용가능포인트 0
         */
        // ContMstEntity cont = contRealDto.getCont();
        if(VdUtil.isNotEqual(contReal.getUseCanYn(), "Y")){
            return 0L;
        }

        // 계약정보상의 계약기간 여부 체크
        if(!DateUtil.isBetweenAp(contReal.getStartDt(), contReal.getEndDt())
            || !DateUtil.isBetweenAp(contReal.getUseStartDt(), contReal.getUseEndDt())){
            return 0L;
        }

        // 사용기간에 제한 있으면 사용종료일 또는 소멸일자로 계산해야 함
        Long realBalPtSum = ptMapper.selectBalPtSum(new PtSearch(memberNo, contReal.getContNo()));
        if(VdUtil.isNotEmpty(contBalPt)) {
            VdUtil.ec(contBalPt < realBalPtSum, BIZ_ERR_001124, "사용 포인트 계산 오류(calcMemberPtMstAblePt)");
            realBalPtSum = contBalPt; // 계약잔여포인트 입력시 사용표인트 계산을 위한 것이므로 잔여포인트합계로 move
        }

        // 잔여포인트가 0이면 사용가능포인트도 0
        if(realBalPtSum <= 0){
            return 0L;
        }

        Long ablePt = realBalPtSum;

        // 1회 사용 제한 체크
        if(VdUtil.isEqual(contReal.getOnceUseLimitTypeCd(), EnumCont.OnceUseLimitType.FIX)){
            if(realBalPtSum < contReal.getOnceUseFixPt()){
                return 0L;
            }else{
                ablePt = contReal.getOnceUseFixPt();
            }
        }else if(VdUtil.isEqual(contReal.getOnceUseLimitTypeCd(), EnumCont.OnceUseLimitType.MIN)
                    || VdUtil.isEqual(contReal.getOnceUseLimitTypeCd(), EnumCont.OnceUseLimitType.BOTH)){
            if(realBalPtSum < contReal.getOnceUseMinPt()){
                return 0L;
            }
        }else if(VdUtil.isEqual(contReal.getOnceUseLimitTypeCd(), EnumCont.OnceUseLimitType.MAX)
                || VdUtil.isEqual(contReal.getOnceUseLimitTypeCd(), EnumCont.OnceUseLimitType.BOTH)){
            if(realBalPtSum > contReal.getOnceUseMaxPt()){
                ablePt = contReal.getOnceUseMaxPt(); // 잔포가 최대사용가능포인트보다 크면 최대사용가능포인트로 잔포 설정
            }
        }else if(VdUtil.isEqual(contReal.getOnceUseLimitTypeCd(), EnumCont.OnceUseLimitType.RATE)){
            if(purchaseAmt != null){
                ablePt = Math.round(purchaseAmt * contReal.getOnceUsePurchaseRate());
            }
        }

        // 기간별
        if(VdUtil.isNotEqual(contReal.getUsePeriodLimitTypeCd(), EnumCont.UsePeriodLimitType.NONE)){
            PtSearch ptSearch = new PtSearch(memberNo, contReal.getContNo());
            String useStartDay = DateUtil.getFirstDayString(contReal.getUsePeriodLimitTypeCd().toString(), DateUtil.getTodayString());
            String useEndDay = DateUtil.getLastDayString(contReal.getUsePeriodLimitTypeCd().toString(), DateUtil.getTodayString());
            ptSearch.setStartDt(DateUtil.convStringToTimestampForStart(useStartDay));
            ptSearch.setEndDt(DateUtil.convStringToTimestampForEnd(useEndDay));

            TrHistDetailSumDto trSum = thdService.getTrHistDetailUseSum(ptSearch);

            // maxCnt 보다 지금 사용하려는 1건 포함한 전체 사용건수보다 크거나 같으면 사용횟수 초과로 사용 불가
            if(contReal.getUsePeriodMaxCnt() <= (trSum.getNetUseCntSum() + 1)){
                return 0L;
            }

            // maxPt 가 전체 사용포인트보다 크면 balPt와 비교하여 계산
            if(contReal.getUsePeriodMaxPt() > trSum.getNetUsePtSum()){
                long remainingMaxPt = contReal.getUsePeriodMaxPt() - trSum.getNetUsePtSum();
                ablePt = remainingMaxPt > ablePt ? ablePt : ablePt - remainingMaxPt;
            }else{
                return 0L;
            }
        }

        return ablePt;
    }

    // 사용취소를 위한 사용 원거래찾기. 원거래일련번호 입력시에는 원거래일련번호로 찾고, 아니면 가장 최근의 사용건으로 정함
    public TrHistEntity getOrglTrHistForUseCancel(Long memberNo, Long ucReqPt, Long orglTrSno){

        TrHistEntity orglTh = null;
        TrHistSearch search = new TrHistSearch();
        search.setMemberNo(memberNo);

        if(VdUtil.isNotEmpty(orglTrSno)) {
            search.setTrSno(orglTrSno);
            orglTh = thService.getTrHist(search);
        }else{
            // 가장 최근 사용거래 조회
            search.setTrGbCd(EnumPoint.TrGb.U001);
            orglTh = thService.getLastOneTrHist(search);
        }

        // TR_HIST 체크
        VdUtil.notEqualEc(orglTh.getMemberNo(), memberNo, BIZ_ERR_001115, "취소 대상 원거래 없음(회원정보와 원거래 내용 불일치)");

        // TR_HIST 체크
        VdUtil.emptyEc(orglTh, BIZ_ERR_001115, "취소 대상 원거래 없음");

        if(VdUtil.isNotEmpty(ucReqPt)){
            VdUtil.ec(ucReqPt > orglTh.getTrPt(), BIZ_ERR_001117, "취소요청 포인트가 원거래 포인트보다 클 수 없음");
        }

        return orglTh;
    }
}
