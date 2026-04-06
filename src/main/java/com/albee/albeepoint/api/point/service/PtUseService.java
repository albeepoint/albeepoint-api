package com.albeepoint.core.biz.point.service;

import com.albeepoint.core.biz.cont.models.EnumCont;
import com.albeepoint.core.biz.cont.models.EnumContRel;
import com.albeepoint.core.biz.cont.models.dto.ContRealDto;
import com.albeepoint.core.biz.cont.models.dto.ContRelSearch;
import com.albeepoint.core.biz.cont.models.dto.ContSearch;
import com.albeepoint.core.biz.cont.models.entity.ContMstEntity;
import com.albeepoint.core.biz.cont.models.entity.ContRelEntity;
import com.albeepoint.core.biz.cont.service.ContMstService;
import com.albeepoint.core.biz.cont.service.ContRelService;
import com.albeepoint.core.biz.member.models.entity.MemberMstEntity;
import com.albeepoint.core.biz.point.models.EnumPoint;
import com.albeepoint.core.biz.point.models.dto.*;
import com.albeepoint.core.biz.point.models.entity.MemberPtMstEntity;
import com.albeepoint.core.biz.point.models.entity.TrHistEntity;
import com.albeepoint.core.biz.point.models.ivo.request.AbleReqIVo;
import com.albeepoint.core.biz.point.models.ivo.request.UseReqIVo;
import com.albeepoint.core.biz.point.models.ivo.response.AbleResultResIVo;
import com.albeepoint.core.biz.point.models.ivo.response.UseResultResIVo;
import com.albeepoint.core.biz.point.models.ivo.response.UsedPtResIVo;
import com.albeepoint.core.biz.point.util.ContRelInfoComparator;
import com.albeepoint.core.biz.point.util.MemberContPtResVoComparator;
import com.albeepoint.core.biz.stat.models.dto.ContRecordInputDto;
import com.albeepoint.core.biz.stat.service.ContRecordService;
import com.albeepoint.core.common.exception.AlbeepointException;
import com.albeepoint.core.common.models.dto.ResultListDto;
import com.albeepoint.core.common.util.CommUtil;
import com.albeepoint.core.common.util.DateUtil;
import com.albeepoint.core.common.util.StrUtil;
import com.albeepoint.core.common.util.VdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

import static com.albeepoint.core.biz.point.models.EnumPoint.TrGb.U001;
import static com.albeepoint.core.biz.point.models.EnumPoint.TrMethodGb.ONLINE;
import static com.albeepoint.core.common.models.ErrorCode.*;


@RequiredArgsConstructor
@Service
@Transactional 
public class PtUseService {
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
    PtUseUtilService ptUseUtilService;
    @Autowired
    ContRecordService contRecService;
    @Autowired
    ContMstService contService;

    public static final Logger log = LogManager.getLogger(PtUseService.class);

    // 사용 선조회
    public AbleResultResIVo getAble(AbleReqIVo reqVo) {
        log.info("getAble process start");

        UseResultResIVo useResultResVo = use(reqVo.getUseReqDto(), false);

        StrUtil.println(useResultResVo.getUsedList(), "사용가능포인트 목록");
        AbleResultResIVo resVo = new AbleResultResIVo();
        resVo.setAblePt(useResultResVo.getAblePt());
        log.info("사용가능포인트 결과 : " + resVo);
        log.info("getAble process end");
        return resVo;
    }

    // 사용처리(사용선조회 포함)
    public UseResultResIVo use(UseReqIVo reqVo, Boolean isForUse) {
        log.info("use process start");
        VdUtil.ec(isForUse & VdUtil.isEmpty(reqVo.getUseReqPt()) && VdUtil.isEmpty(reqVo.getPurchaseAmt())
                , BIZ_ERR_001113, "사용요청 포인트 또는 구매금액 필수 입력");

        // 필수 입력 체크 및 회원, 기관, 지점 정보 설정
        UseReqDto req = (UseReqDto) ptUtilService.checkAndSetBasePtReq(reqVo.getUseReqDto());
        req.setTrGbCd(U001);
        log.info("input check completed");

        List<PtReqDto> ptUseReqList = new ArrayList<>(); // 실제 사용처리 내역
        Long memberNo = req.getMember().getMemberNo();
        Long useReqPt = req.getUseReqPt();
        UseResultResIVo resVo = new UseResultResIVo(useReqPt);

        // 사용대상 계약 목록 조회
        Map<Long, ContRealDto> contRealMap = getUsableContRealList(req);
        log.info("use target contRealList creation");
        VdUtil.ec(!isForUse && contRealMap.isEmpty(), BIZ_ERR_001112, "사용가능한 보유 포인트 목록 없음");

        // 사용대상 조회. 사용순서, 잔여포인트 포함. 계약기간/계약사용기간(사용취소가능기간) 쿼리에서 체크
        PtSearch ptSearch = new PtSearch() ;
        ptSearch.setMemberNo(memberNo);
        // ptSearch.setReqPt(useReqPt); // 일단 잔액 있는 모든 PT 목록 추출
        ptSearch.setContNoList(contRealMap.keySet().stream().collect(Collectors.toList()));
        // 보유포인트(PT_MST) 목록 조회 : 잔여포인트 존재하고, 1회사용제한(FIX, MAX, BOTH, RATE), 사용기간제한 체크하여 사용가능한 포인트목록 추출
        List<PtMstTargetDto> useTargetList = ptService.getUseTargetListByReqPt(ptSearch, contRealMap);
        VdUtil.ec(!isForUse && VdUtil.isEmpty(useTargetList), BIZ_ERR_001125, "사용 가능 포인트 없음");

        // 계약별 잔여포인트 합계
        Map<Long, Long> contBalPtMap = useTargetList.stream().collect(Collectors.toMap(e -> e.getContNo(), e -> e.getBalPt(), Long::sum));
        log.info("계약별 잔여포인트 합산 결과 : " + contBalPtMap);

        Long totalBalPt = useTargetList.stream().collect(Collectors.summingLong(PtMstTargetDto::getBalPt));
        Long remainingReqPt = VdUtil.isNotEmpty(useReqPt) ? useReqPt : totalBalPt;
        Long totalAblePt = 0L;

        VdUtil.ec(!isForUse && (totalBalPt < remainingReqPt), BIZ_ERR_001122, "사용가능포인트 부족");

        // 계약별로 1회 사용조건, 기간별 사용제한, 지점별 사용제한 불만족시 제거
        for(Map.Entry<Long, Long> entry : contBalPtMap.entrySet()){
            Long contNo = entry.getKey();
            Long contAblePt = ptUseUtilService.calcMemberPtMstAblePt(
                    contRealMap.get(contNo), contBalPtMap.get(contNo), memberNo, req.getPurchaseAmt());


            // 사용가능 잔여포인트 없으면 해당 계약에 대한 PT는 대상에서 제거
            if(contAblePt <= 0) {
                useTargetList = removeContTarget(useTargetList, contNo);
                continue;
            }

            contBalPtMap.put(contNo, contAblePt); // Map의 사용가능잔여포인트 업데이트

            // 1회사용제한 계약부터 먼저 사용 처리
            if(VdUtil.isNotEmpty(contRealMap.get(contNo).getOnceUseLimitTypeCd())
                && VdUtil.isNotEqual(contRealMap.get(contNo).getOnceUseLimitTypeCd(), EnumCont.OnceUseLimitType.NONE)){

                List<PtMstTargetDto> targetList = useTargetList.stream()
                        .filter(t -> VdUtil.isEqual(contNo, t.getContNo())).collect(Collectors.toList());

                //Long contUseReqPtSum = targetList.stream().collect(Collectors.summingLong(PtMstTargetDto::getBalPt));

                // 사용처리할 포인트 존재할때 사용 처리
                //if(contBalPtMap.get(contNo) >= contUseReqPtSum){
                    List<PtReqDto> contUseLimitPtReqList = usePt(req, targetList, contBalPtMap, remainingReqPt, isForUse);
                    log.info("1회사용제한 우선 사용 처리 : " + contUseLimitPtReqList);
                    ptUseReqList.addAll(contUseLimitPtReqList);
                    useTargetList = removeContTarget(useTargetList, contNo); // 사용처리한 PT는 모수 목록에서 삭제
                    Long usedLimitPt = contUseLimitPtReqList.stream().collect(Collectors.summingLong(PtReqDto::getUseReqPt));
                    remainingReqPt = remainingReqPt - usedLimitPt;
                //}
            }
        }
        log.info("계약별 잔여포인트 중 계약조건 체크 후 사용 가능 포인트 : " + contBalPtMap);

        if(VdUtil.isNotEmpty(useTargetList)) {
            List<PtReqDto> contUsePtReqList = usePt(req, useTargetList, contBalPtMap, remainingReqPt, isForUse);
            log.info("1회사용제한 미해당 사용 처리 : " + contUsePtReqList);
            ptUseReqList.addAll(contUsePtReqList);
            Long usedPt = contUsePtReqList.stream().collect(Collectors.summingLong(PtReqDto::getUseReqPt));
            remainingReqPt = remainingReqPt - usedPt;
        }
        totalAblePt = ptUseReqList.stream().collect(Collectors.summingLong(PtReqDto::getUseReqPt));
        resVo.setAblePt(totalAblePt);
        log.info("총사용가능포인트 : " + totalAblePt);

        if(!isForUse){
            resVo.setUsedPt(0L);
            log.info("사용 선조회 처리 끝. 총사용가능포인트(ablePt)=[" + totalAblePt + "]");
            return resVo;
        }

        long trSeq = 0;
        Long totalUsePt = 0L;
        for(PtReqDto ptReqDto : ptUseReqList){
            ptReqDto.setTrSeq(++trSeq);
            totalUsePt = totalUsePt + ptReqDto.getUseReqPt();
        }
        req.setTrPt(totalUsePt);
        VdUtil.ec(totalUsePt < 0, BIZ_ERR_001124, "사용 포인트 계산 오류");
        VdUtil.ec(useReqPt > totalUsePt, BIZ_ERR_001122, "사용가능 포인트 부족");

        // 포인트마스터 일괄 업데이트
        ptService.useMultiPtMst(ptUseReqList);

        // 회원포인트마스터 업데이트
        // 회원포인트마스터의 경우 계약단위로 업데이트를 하는데 쿠폰형계약은 다건 존재하므로 먼저 Map 을 이용해서 계약단위로 취합
        Map<Long, Long> mbrPtMap = ptUseReqList.stream().collect(Collectors.toMap(e -> e.getMemberPtNo(), e -> e.getUseReqPt(), Long::sum));
        for(Map.Entry<Long, Long> entry : mbrPtMap.entrySet()){
            MemberPtDto mbrPtDto = new MemberPtDto(entry.getKey());
            mbrPtDto.setUsePt(entry.getValue());
            mbrPtService.updateUseMemberPtMst(mbrPtDto);
        }

        // 거래내역, 거래내역상세 인서트
        log.info("use tr history insert");
        Long trSno = setUseTrHist(req, ptUseReqList);

        // 응답용 실제 사용 처리된 내역 목록
        List<UsedPtResIVo> usedList = new ArrayList();  // 실제 사용취소 처리된 포인트 내역
        for(PtReqDto ptReq : ptUseReqList ){
            usedList.add(new UsedPtResIVo(ptReq));
        }

        resVo.setUsedPt(totalUsePt);
        resVo.setTrSno(trSno);
        resVo.setUsedList(usedList);
        log.info("use process end");
        return resVo;
    }

    // PT별 사용 추출
    private List<PtReqDto> usePt(UseReqDto req, List<PtMstTargetDto> useTargetList, Map<Long, Long> contBalPtMap, Long useReqPt, Boolean isForUse){
        List<PtReqDto> ptReqList = new ArrayList<>();
        if(useReqPt <= 0) return ptReqList;

        Long remainingReqPt = useReqPt;
        for(PtMstTargetDto targetDto : useTargetList){
            VdUtil.ec(remainingReqPt < 0, BIZ_ERR_001124, "사용 포인트 계산 오류(remainingReqPt)");
            if(remainingReqPt == 0) break;             // 사용요청포인트를 모두 사용처리한 경우 종료
            Long targetContNo = targetDto.getContNo();
            Long contBalPt = contBalPtMap.get(targetContNo);
            if(contBalPt <= 0) continue;               // 해당 계약 잔여포인트 모두 소진. 다음으로 skip

            // 사용가능포인트 계산
            Long tmpUsePt = ptUseUtilService.calcPtAblePt(contBalPt
                    , targetDto, req.getMember(), remainingReqPt, req.getPurchaseAmt(), isForUse);
            targetDto.setAblePt(tmpUsePt);  // 사용가능 포인트 계산
            if(tmpUsePt <= 0){
                log.info("사용가능포인트 0 : " + targetDto);
                continue;
            }

            remainingReqPt = remainingReqPt - tmpUsePt;
            Long befContBalPt = contBalPt;
            contBalPt = contBalPt - tmpUsePt;
            VdUtil.ec(contBalPt < 0, BIZ_ERR_001124, "사용 포인트 계산 오류(contBalPt)");
            contBalPtMap.put(targetContNo, contBalPt);  // 계약별 잔여포인트를 사용가능포인트만큼 차감(사용제한 조건 체크용)

            PtReqDto ptReqDto = new PtReqDto(req, targetDto.getContReal(), targetDto.getPtNo(), tmpUsePt);
            ptReqDto.setMemberPtNo(targetDto.getMemberPtNo());
            ptReqDto.setContNo(targetContNo);
            ptReqDto.setContNm(targetDto.getContNm());
            ptReqDto.setBalPt(targetDto.getBalPt());
            ptReqDto.setContBalPt(befContBalPt);
            ptReqDto.setPtNo(targetDto.getPtNo());
            ptReqDto.setMemberPtNo(targetDto.getMemberPtNo());
            ptReqList.add(ptReqDto);
        }
        StrUtil.println(ptReqList, "사용대상");
        return ptReqList;
    }

    // 사용대상목록에서 특정 계약 삭제
    private List<PtMstTargetDto> removeContTarget(List<PtMstTargetDto> targetList, Long contNo){
        Iterator<PtMstTargetDto> iterator = targetList.iterator();
        while(iterator.hasNext()) {
            PtMstTargetDto target = iterator.next();
            if(VdUtil.isEqual(contNo, target.getContNo())) {
                iterator.remove();
            }
        }

        return targetList;
    }

    // 사용이력 저장
    private Long setUseTrHist(UseReqDto req, List<PtReqDto> useTargetResultList){
        log.info("사용 거래내역 저장 시작");

        Long orgNo = req.getOrgNo();
        Long brchNo = VdUtil.isNotEmpty(req.getBrch()) ? req.getBrch().getBrchNo() : null;

        // TR_HIST 인서트
        TrHistEntity trHist = new TrHistEntity();
        long newTrHistSno = thService.getNewTrSno();
        trHist.setTrSno(newTrHistSno);
        trHist.setMemberNo(req.getMemberNo());
        trHist.setTrGbCd(U001);
        trHist.setTrMethodGbCd(ONLINE);
        trHist.setOrgNo(orgNo);
        trHist.setBrchNo(brchNo);
        trHist.setTrPt(req.getTrPt());
        trHist.setTrDy(DateUtil.getNowTimeZoneDayString());
        thService.regTrHist(trHist);

        // TR_HIST_DETAIL 인서트
        TrHistSearch trHistSearch = new TrHistSearch();
        trHistSearch.setMemberNo(trHist.getMemberNo());
        trHistSearch.setTrSno(newTrHistSno);
        trHistSearch.setUseTargetList(useTargetResultList);
        thdService.regUseMultiTrHistDetail(trHistSearch);

        // 총발행내역 저장
        List<ContRecordInputDto> contRecInList = new ArrayList<>();

        for(PtReqDto useTgt : useTargetResultList){
            req.setContNo(useTgt.getContNo());
            for(ContRecordInputDto input : contRecInList){
                if(VdUtil.isEqual(input.getContNo(), useTgt.getContNo())){
                    input.setUsePt(input.getUsePt() + useTgt.getUseReqPt());
                    break;
                }
            }
            // 기존에 없던 요소는 add
            contRecInList.add(new ContRecordInputDto(useTgt.getContNo(), orgNo, brchNo, useTgt.getUseReqPt()));
        }
        contRecService.useRecord(contRecInList);
        log.info("사용 거래내역 저장 완료");

        return newTrHistSno;
    }

    // 사용가능 계약 목록 조회
    private Map<Long, ContRealDto> getUsableContRealList(UseReqDto req){
        Map<Long, ContRealDto> contRealMap = new HashMap<>();

        // 특정 계약번호가 입력되었다면 해당 계약만 체크함
        if(VdUtil.isNotEmpty(req.getContNo())){
            // 계약정보(서브계약존재시 해당 서브계약), 계약기간, 계약사용기간, 기관유효성, 지점유효성, 계약기관, 계약지점
            ContRealDto contRealDto = ptUtilService.getContForTr(req.getCont(), req.getOrgNo(), req.getMemberNo());
            contRealMap.put(contRealDto.getContNo(), contRealDto);
            return contRealMap;
        }

        // 특정 계약번호 가 입력되지 않은 경우 전체 사용 가능한 계약이 대상
        // 회원계약마스터 목록 조회
        PtSearch ptSearch = new PtSearch();
        ptSearch.setBaseDt(DateUtil.getNowDateTimeString());
        ptSearch.setBaseBalPt(0L);
        ptSearch.setContStsCd(EnumCont.ContSts.NORMAL);
        ptSearch.setMemberNo(req.getMember().getMemberNo());
        ResultListDto<MemberPtMstEntity> mbrPtList = mbrPtService.getMemberPtMstList(ptSearch);
        if(VdUtil.isEmpty(mbrPtList)){
            return contRealMap;  // 사용가능한 계약 목록이 없으면 그냥 리턴
        }

        // 사용가능한 계약이 1건만이라면 합산사용(MIX_USE_YN)과 무관함.
        // 계약정보(서브계약존재시 해당 서브계약). 계약기간, 계약사용기간, 기관유효성, 지점유효성, 계약기관, 계약지점
        if (mbrPtList.getList().size() == 1){
            log.info("유효한 보유 계약 1건 존재 : [" + mbrPtList.getList().get(0).toString() + "]");
            ContMstEntity cont = contService.getContMstWec(new ContSearch(mbrPtList.getList().get(0).getContNo()));
            ContRealDto contRealDto = ptUtilService.getContForTr(cont, req.getOrgNo(), req.getMemberNo());
            contRealMap.put(contRealDto.getContNo(), contRealDto);
            return contRealMap;
        }

        // 사용가능한 계약이 여러건이면 합산사용(MIX_USE_YN) 가능한 계약만 사용대상임
        BasePtReqDto bprd = new BasePtReqDto();
        CommUtil.objectCopy(req, bprd);
        for(MemberPtMstEntity mbrPtEty : mbrPtList.getList()){
            try {
                bprd.setContNo(mbrPtEty.getContNo());
                ContMstEntity cont = contService.getContMstWec(new ContSearch(mbrPtEty.getContNo()));
                if(cont.getMixUseYn() != null && "Y".equalsIgnoreCase(cont.getMixUseYn())){
                    ContRealDto contRealDto = ptUtilService.getContForTr(cont, req.getOrgNo(), req.getMemberNo());
                    if(!contRealMap.containsKey(contRealDto.getContNo())){
                        contRealMap.put(contRealDto.getContNo(), contRealDto);
                    }
                }
            }catch(AlbeepointException ae) {
                log.info("사용불가 계약 : [" + mbrPtEty.getContNo() + "]");
                log.info(ae.toString());
            }
        }

        return contRealMap;
    }
}
