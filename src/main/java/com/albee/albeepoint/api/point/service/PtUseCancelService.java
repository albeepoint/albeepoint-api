package com.albeepoint.core.biz.point.service;

import com.albeepoint.core.biz.point.models.EnumPoint;
import com.albeepoint.core.biz.point.models.dto.*;
import com.albeepoint.core.biz.point.models.entity.TrHistDetailEntity;
import com.albeepoint.core.biz.point.models.entity.TrHistEntity;
import com.albeepoint.core.biz.point.models.ivo.request.IsuCancelReqIVo;
import com.albeepoint.core.biz.point.models.ivo.request.UseCancelReqIVo;
import com.albeepoint.core.biz.point.models.ivo.response.IsuCancelResultResIVo;
import com.albeepoint.core.biz.point.models.ivo.response.MemberPtResIVo;
import com.albeepoint.core.biz.point.models.ivo.response.UseCancelResultResIVo;
import com.albeepoint.core.biz.point.models.ivo.response.UseCanceledPtResIVo;
import com.albeepoint.core.biz.stat.models.dto.ContRecordInputDto;
import com.albeepoint.core.biz.stat.service.ContRecordService;
import com.albeepoint.core.common.models.dto.ResultListDto;
import com.albeepoint.core.common.util.DateUtil;
import com.albeepoint.core.common.util.VdUtil;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.albeepoint.core.biz.point.models.EnumPoint.TrGb.UC01;
import static com.albeepoint.core.biz.point.models.EnumPoint.TrMethodGb.ONLINE;
import static com.albeepoint.core.common.models.ErrorCode.*;


@RequiredArgsConstructor
@Service
@Transactional 
public class PtUseCancelService {
    @Autowired
    PtMstService ptService;
    @Autowired
    MemberPtMstService mbrPtService;
    @Autowired
    TrHistService thService;
    @Autowired
    TrHistDetailService thdService;
    @Autowired
    ContRecordService contRecService;
    @Autowired
    PtUtilService ptUtilService;
    @Autowired
    PtUseUtilService ptUseUtilService;
    @Autowired
    PtIsuCancelService ptIsuCancelService;

    public static final Logger log = LogManager.getLogger(PtUseCancelService.class);

    /*
        사용취소
            원거래번호 입력 : 원사용거래에 대해 모든 사용의 취소처리. 사용취소요청포인트 입력시 해당 포인트만큼 사용취소
            원거래번호 + 사용취소요청포인트 입력 : 원거래내에서 사용취소요청포인트 만큼 사용취소
            사용취소요청포인트 입력 : 직전 사용거래에 대해 입력한 사용취소요청포인트만큼 사용취소
     */
    public UseCancelResultResIVo useCancel(UseCancelReqIVo reqVo) {
        log.info("use cancel process start");
        VdUtil.ec(VdUtil.isEmpty(reqVo.getUseCancelReqPt()) && VdUtil.isEmpty(reqVo.getOrglTrSno())
                , BIZ_ERR_001114, "사용취소 대상 필수 입력");

        // 필수 입력 체크
        UseCancelReqDto req = (UseCancelReqDto) ptUtilService.checkAndSetBasePtReq(reqVo.getUseCancelReqDto());
        req.setTrGbCd(UC01);
        log.info("input check completed");

        // 원거래(TR_HIST) 찾기. 원거래일련번호 입력시에는 원거래일련번호로 찾고, 아니면 가장 최근의 사용건으로 정함
        TrHistEntity orglTh = ptUseUtilService.getOrglTrHistForUseCancel(req.getMemberNo(), req.getUseCancelReqPt(), req.getOrglTrSno());
        // req.setOrglTrSno(orglTh.getTrSno());
        req.setOrglTrSno(orglTh.getTrSno());

        Long ucReqPt = VdUtil.isNotEmpty(req.getUseCancelReqPt()) ? req.getUseCancelReqPt() : orglTh.getTrPt();

        // 사용취소요청포인트 설정. req.getUseCancelReqPt()
        TrHistDetailSumDto detailSum = checkUseCancelReqPt(req);
        if(VdUtil.isEmpty(ucReqPt)) {
            // 취소요청PT 미입력. 미취소 잔여사용PT로 설정
            req.setUseCancelReqPt(detailSum.getNetUsePtSum());
        }else{
            // 취소요청PT 입력. 미취소 잔여사용PT가 부족 오류
            VdUtil.ec(ucReqPt > detailSum.getNetUsePtSum()
                    , BIZ_ERR_001126, "사용 취소 가능 잔여 포인트 부족");
            // 사용취소요청포인트 입력값만큼 취소하므로 추가 입력한 그대로 이므로 추가 설정 안함.
        }

        // 사용취소대상 조회. 취소순서, 취소포인트 포함. 계약기간/계약사용기간(사용취소가능기간)/포인트사용기간 쿼리에서 체크
        PtSearch ptSearch = new PtSearch();
        ptSearch.setTrSno(req.getOrglTrSno());
        ptSearch.setReqPt(ucReqPt);
        List<PtMstTargetDto> useCancelTargetList = ptService.getUseCancelTargetListByReqPt(ptSearch);
        VdUtil.emptyEc(useCancelTargetList, BIZ_ERR_001127, "사용 취소 가능 거래내역 없음");
        ptSearch.setMemberNo(req.getMemberNo());

        List<Long> contNoList = useCancelTargetList.stream().map(PtMstTargetDto::getContNo).distinct().collect(Collectors.toList());
        ptSearch.setContNoList(contNoList);
        Map<Long, Long> contBalPtMap = ptService.getMultiContBalPtSum(ptSearch);
        log.info("계약별 잔여포인트 결과 : " + contBalPtMap);

        // 포인트마스터 일괄 업데이트
        Map<Long, Long> mbrPtMap = new HashMap<>();  // 회원포인트마스터 업데이트 대상
        List<PtReqDto> ptReqDtoList = new ArrayList<>();
        Long totalUseCancelPt = 0L;
        long trSeq = 0;
        for(PtMstTargetDto ucTgt : useCancelTargetList ){
            Long befContBalPt = contBalPtMap.get(ucTgt.getContNo());
            contBalPtMap.put(ucTgt.getContNo(), befContBalPt + ucTgt.getUseCancelPt());

            PtReqDto ptReqDto = new PtReqDto(ucTgt.getPtNo(), ucTgt.getUseCancelPt());
            ptReqDto.setTrSeq(++trSeq);
            ptReqDto.setContNo(ucTgt.getContNo());
            ptReqDto.setContNm(ucTgt.getContNm());
            ptReqDto.setMemberPtNo(ucTgt.getMemberPtNo());
            ptReqDto.setTrPt(ucTgt.getUseCancelPt());
            ptReqDto.setReqPt(ucTgt.getUseCancelPt());
            ptReqDto.setUseCancelReqPt(ucTgt.getUseCancelPt());
            ptReqDto.setPtNo(ucTgt.getPtNo());
            ptReqDto.setBalPt(ucTgt.getBalPt());
            ptReqDto.setContBalPt(befContBalPt);
            ptReqDto.setOrglTrSno(ucTgt.getOrglTrSno());
            ptReqDtoList.add(ptReqDto);

            totalUseCancelPt = totalUseCancelPt + ucTgt.getUseCancelPt();

            // 회원포인트마스터 업데이트 위한 회원포인트마스터별 사용취소포인트 합산
            if(mbrPtMap.containsKey(ucTgt.getMemberPtNo())){
                mbrPtMap.put(ucTgt.getMemberPtNo(), mbrPtMap.get(ucTgt.getMemberPtNo()) + ucTgt.getUseCancelPt());
            }else{
                mbrPtMap.put(ucTgt.getMemberPtNo(), ucTgt.getUseCancelPt());
            }
        }

        req.setTrPt(totalUseCancelPt);

        ptSearch.setPtReqList(ptReqDtoList);
        ptService.useCancelMultiPtMst(ptReqDtoList);

        // 회원포인트마스터 업데이트
        // 회원포인트마스터의 경우 계약단위로 업데이트를 하는데 쿠폰형계약은 다건 존재하므로 먼저 Map 을 이용해서 계약단위로 취합
        for(Map.Entry<Long, Long> entry : mbrPtMap.entrySet()){
            MemberPtDto mbrPtDto = new MemberPtDto(entry.getKey());
            mbrPtDto.setUseCancelPt(entry.getValue());
            mbrPtService.updateUseCancelMemberPtMst(mbrPtDto);
        }

        // 거래이력계약 인서트
        Long trSno = setUseCancelTrHist(req, ptReqDtoList);

        // 응답용 실제 사용취소 처리된 내역 목록
        List<UseCanceledPtResIVo> useCanceledList = new ArrayList();  // 실제 사용취소 처리된 포인트 내역
        for(PtReqDto ptReq : ptReqDtoList) {
            useCanceledList.add(new UseCanceledPtResIVo(ptReq));
        }

        // 회원포인트 현황 응답
        UseCancelResultResIVo useCancelResult = new UseCancelResultResIVo();
        useCancelResult.setUseCancelReqPt(ucReqPt);
        useCancelResult.setUseCanceledPt(totalUseCancelPt);
        useCancelResult.setTrSno(trSno);
        useCancelResult.setUseCanceledList(useCanceledList);
        log.info("use cancel process end");

        // 자동 적립되었던 건 적립 취소 처리
        cancelToUseAddIsu(req);

        return useCancelResult;
    }


    // 사용취소이력 저장
    private Long setUseCancelTrHist(UseCancelReqDto req, List<PtReqDto> useCancelTargetResultList){
        log.info("사용 취소 거래내역 저장 시작");

        Long orgNo = req.getOrgNo();
        Long brchNo = VdUtil.isNotEmpty(req.getBrch()) ? req.getBrch().getBrchNo() : null;
        Long orglTrSno = req.getOrglTrSno();

        // TR_HIST 인서트
        TrHistEntity trHist = new TrHistEntity();
        long newTrHistSno = thService.getNewTrSno();
        trHist.setTrSno(newTrHistSno);
        trHist.setMemberNo(req.getMemberNo());
        trHist.setTrGbCd(UC01);
        trHist.setTrMethodGbCd(ONLINE);
        trHist.setOrgNo(orgNo);
        trHist.setBrchNo(brchNo);
        trHist.setTrPt(req.getTrPt());
        trHist.setTrDy(DateUtil.getNowTimeZoneDayString());
        trHist.setOrglTrSno(orglTrSno);
        thService.regTrHist(trHist);

        // TR_HIST_DETAIL 인서트
        TrHistSearch trHistSearch = new TrHistSearch();
        trHistSearch.setMemberNo(trHist.getMemberNo());
        trHistSearch.setTrSno(newTrHistSno);
        trHistSearch.setOrglTrSno(orglTrSno);
        trHistSearch.setUseCancelTargetList(useCancelTargetResultList);
        thdService.regUseCancelMultiTrHistDetail(trHistSearch);

        // 총발행내역 저장
        //List<PtReqDto> ptReqDtoList = new ArrayList<>();
        List<ContRecordInputDto> contRecInList = new ArrayList<>();

        for(PtReqDto ucTgt : useCancelTargetResultList){
            // ptReqDtoList.add(new PtReqDto(req.getMemberNo(), ucTgt.getContNo(), orgNo, ucTgt.getPtNo(), ucTgt.getUseCancelPt()));
            for(ContRecordInputDto input : contRecInList){
                if(VdUtil.isEqual(input.getContNo(), ucTgt.getContNo())){
                    input.setUsePt(input.getUsePt() - ucTgt.getUseCancelReqPt());
                    break;
                }
            }
            // 기존에 없던 요소는 add
            contRecInList.add(new ContRecordInputDto(ucTgt.getContNo(), orgNo, brchNo, ucTgt.getUseCancelReqPt()));
        }

        // 총발행내역 저장
        contRecService.useRecord(contRecInList);
        log.info("사용 거래내역 저장 완료");
        return newTrHistSno;
    }


    // 사용취소요청포인트가 실제 기사용취소포인트를 포함했을 때 사용포인트를 초과하면 에러
    private TrHistDetailSumDto checkUseCancelReqPt(UseCancelReqDto req){
        PtSearch search = new PtSearch();
        search.setMemberNo(req.getMemberNo());
        search.setTrSno(req.getOrglTrSno());
        TrHistDetailSumDto stat = thdService.getTrHistDetailUseSum(search);
        VdUtil.ec(VdUtil.isNotEmpty(req.getUseCancelReqPt()) && stat.getNetUsePtSum() < req.getUseCancelReqPt()
                , BIZ_ERR_001119, "기취소포인트를 포함한 취소요청 포인트가 원거래 포인트보다 클 수 없음");

        VdUtil.ec(stat.getUseCnt() > 0 && stat.getNetUsePtSum() <= 0, BIZ_ERR_001118, "이미 취소된 거래");
        return stat;
    }

    // 사용취소된 건이 애초에 사용되면서 자동 추가 적립되었던 건을 적립 취소 처리
    // 추가 적립되었던 원인이 사라졌으므로 적립도 취소되어야 함
    private void cancelToUseAddIsu(UseCancelReqDto req){
        log.info("자동 추가 적립건 적립 취소 시작");
        TrHistSearch thOrglSearch = new TrHistSearch();
        thOrglSearch.setTrGbCd(EnumPoint.TrGb.S001);
        thOrglSearch.setTrMethodGbCd("USE_ADD_ISU");
        thOrglSearch.setOrglTrSno(req.getOrglTrSno());
        ResultListDto<TrHistEntity> orgThListResultDto = thService.getTrHistList(thOrglSearch);
        if(VdUtil.isNotEmpty(orgThListResultDto) && VdUtil.isNotEmpty(orgThListResultDto.getList())){
            for(TrHistEntity isuTr : orgThListResultDto.getList()) {
                log.info("적립 취소 시작 : " + isuTr);
                TrHistSearch thdSearch = new TrHistSearch();
                thdSearch.setMemberNo(req.getMemberNo());
                thdSearch.setTrSno(req.getOrglTrSno());
                TrHistDetailEntity thd = thdService.getTrHistDetailWec(thdSearch);
                IsuCancelReqIVo icReq = new IsuCancelReqIVo();
                icReq.setMemberId(req.getMemberId());
                icReq.setContNo(thd.getContNo());
                icReq.setOrgCd(req.getOrgCd());
                icReq.setBrchCd(req.getBrchCd());
                icReq.setOrglTrSno(isuTr.getTrSno());
                IsuCancelResultResIVo icRes = ptIsuCancelService.issueCancel(icReq);
                log.info("적립 취소 완료 : " + icRes);
            }
        }
        log.info("자동 추가 적립건 적립 취소 종료");
    }
}
