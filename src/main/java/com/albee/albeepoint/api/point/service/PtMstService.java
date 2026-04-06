package com.albeepoint.core.biz.point.service;

import com.albeepoint.core.biz.cont.models.dto.ContRealDto;
import com.albeepoint.core.biz.point.models.EnumPoint;
import com.albeepoint.core.biz.point.models.dto.CalcBefAfrBalDto;
import com.albeepoint.core.biz.point.models.dto.PtMstTargetDto;
import com.albeepoint.core.biz.point.models.dto.PtReqDto;
import com.albeepoint.core.biz.point.models.dto.PtSearch;
import com.albeepoint.core.biz.point.models.entity.PtMstEntity;
import com.albeepoint.core.biz.point.repository.PtMstMapper;
import com.albeepoint.core.common.models.dto.ResultListDto;
import com.albeepoint.core.common.util.VdUtil;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.albeepoint.core.common.models.ErrorCode.*;


@RequiredArgsConstructor
@Service
@Transactional
public class PtMstService {
    @Autowired
    PtMstMapper ptMapper;

    @Autowired
    PtUseUtilService ptUseUtilService;

    public static final Logger log = LogManager.getLogger(PtMstService.class);

    public PtMstEntity regPtMst(PtMstEntity dom) {
        long newPtNo = ptMapper.selectSeqPtMstNo();
        dom.setPtNo(newPtNo);
        ptMapper.insertPtMst(dom);
        return ptMapper.selectPtMst(new PtSearch(newPtNo));
    }


    public void isuPtMst(PtReqDto ptReqDto){
        VdUtil.ec(ptMapper.updateIsuPtMst(ptReqDto) <= 0, BIZ_ERR_001042);
    }

    public void isuCancelPtMst(PtReqDto ptReqDto){
        int updCnt = ptMapper.updateIsuCancelPtMst(ptReqDto);
        VdUtil.ec(updCnt <= 0, BIZ_ERR_001042);
        log.info("updateIsuCancelPtMst Issue Cancel PtMst udpate. updCnt = [" + updCnt + "]");
    }

    public void isuCancelMultiPtMst(List<PtReqDto> ptReqDtoList){
        if(VdUtil.isEmpty(ptReqDtoList)){
            return;
        }

        PtSearch isuCancelUpdParam = new PtSearch();
        isuCancelUpdParam.setPtReqList(ptReqDtoList);
        int updCnt = ptMapper.updateMultiIsuCancelPtMst(isuCancelUpdParam);
        VdUtil.ec(updCnt <= 0, BIZ_ERR_001042);
        log.info("isuCancelMultiPtMst : Issue Cancel PtMst udpate. updCnt = [" + updCnt + "]");
    }

    // 포인트마스터 업데이트
    public void useMultiPtMst(List<PtReqDto> ptReqDtoList){
        if(VdUtil.isEmpty(ptReqDtoList)){
            return;
        }

        if(ptReqDtoList.size() <= 50) {
            PtSearch useUpdParam = new PtSearch();
            useUpdParam.setPtReqList(ptReqDtoList);
            int updCnt = ptMapper.updateMultiUsePtMst(useUpdParam);
            VdUtil.ec(updCnt <= 0, BIZ_ERR_001042, "포인트마스터 사용 일괄 업데이트 오류");
            log.info("useMultiPtMst : Use PtMst udpate. updCnt = [" + updCnt + "]");
            return;
        }

        int cnt = 0;
        int totalCnt = ptReqDtoList.size();

        while(cnt >= totalCnt){
            PtSearch useUpdParam = new PtSearch();
            List<PtReqDto> list = new ArrayList<>();
            for(int i = 0; i < 50 ; i++){
                if(cnt <= totalCnt){
                    break;
                }
                list.add(ptReqDtoList.get(cnt++));
            }
            useUpdParam.setPtReqList(list);
            int updCnt = ptMapper.updateMultiUsePtMst(useUpdParam);
            VdUtil.ec(updCnt < 1, BIZ_ERR_001046);
            log.info("useMultiPtMst : Use PtMst udpate. updCnt = [" + updCnt + "]");
        }
    }


    public void useCancelMultiPtMst(List<PtReqDto> ptReqDtoList){
        if(VdUtil.isEmpty(ptReqDtoList)){
            return;
        }

        if(ptReqDtoList.size() <= 50) {
            PtSearch useCancelUpdParam = new PtSearch();
            useCancelUpdParam.setPtReqList(ptReqDtoList);
            int updCnt = ptMapper.updateMultiUseCancelPtMst(useCancelUpdParam);
            VdUtil.ec(updCnt <= 0, BIZ_ERR_001042, "포인트마스터 사용 일괄 업데이트 오류");
            log.info("useCancelMultiPtMst : Use Cancel PtMst udpate. updCnt = [" + updCnt + "]");
            return;
        }

        int cnt = 0;
        int totalCnt = ptReqDtoList.size();

        while(cnt >= totalCnt){
            PtSearch useCancelUpdParam = new PtSearch();
            List<PtReqDto> list = new ArrayList<>();
            for(int i = 0; i < 50 ; i++){
                if(cnt <= totalCnt){
                    break;
                }
                list.add(ptReqDtoList.get(cnt++));
            }
            useCancelUpdParam.setPtReqList(list);
            int updCnt = ptMapper.updateMultiUseCancelPtMst(useCancelUpdParam);
            VdUtil.ec(updCnt < 1, BIZ_ERR_001046);
            log.info("useCancelMultiPtMst : UseCancel PtMst udpate. updCnt = [" + updCnt + "]");
        }
    }

    public PtMstEntity getPtMst(PtSearch dom) {
        return ptMapper.selectPtMst(dom);
    }

    public PtMstEntity getPtMstWec(PtSearch dom) {
        return (PtMstEntity)VdUtil.emptyEc(ptMapper.selectPtMst(dom), BIZ_ERR_001048);
    }

    public List<PtMstTargetDto> getUseTargetListByReqPt(PtSearch dom, Map<Long, ContRealDto> contRealMap){
        List<PtMstTargetDto> ptTargetList = ptMapper.selectUseTargetListByReqPt(dom);
        for(PtMstTargetDto targetDto : ptTargetList) {
            targetDto.setContReal(contRealMap.get(targetDto.getContNo()));
        }

        return ptTargetList;
    }

    public List<PtMstTargetDto> getUseCancelTargetListByReqPt(PtSearch dom){
        return ptMapper.selectUseCancelTargetListByReqPt(dom);
    }

    public List<PtMstTargetDto> getBalPtTargetListByReqPt(PtSearch dom){
        return ptMapper.selectBalPtTargetListByReqPt(dom);
    }


    public Map<Long, Long> getMultiContBalPtSum(PtSearch dom){
        List<PtReqDto> contBalPtList = ptMapper.selectMultiContBalPtSum(dom);
        Map<Long, Long> contBalPtMap = contBalPtList.stream().collect(Collectors.toMap(e -> e.getContNo(), e -> e.getContBalPt(), Long::sum));
        return contBalPtMap;
    }

    public void usePtMst(PtReqDto ptReqDto){
        VdUtil.ec(ptMapper.updateUsePtMst(ptReqDto) <= 0, BIZ_ERR_001042);
    }

    public void useCancelPtMst(PtReqDto ptReqDto){
        VdUtil.ec(ptMapper.updateUseCancelPtMst(ptReqDto) <= 0, BIZ_ERR_001042);
    }

    public ResultListDto<PtMstEntity> getPtMstList(PtSearch dom) {
        ResultListDto<PtMstEntity> result = new ResultListDto<>();
        Long totalCnt = ptMapper.selectPtMstListTotalCnt(dom);
        result.setTotalCnt(totalCnt != null ? totalCnt : 0L);
        if(result.getTotalCnt() > 0){
            List<PtMstEntity> list = ptMapper.selectPtMstList(dom);
            result.setList(list);
            result.setPageCnt(list != null && list.size() > 0 ? list.size() : 0);
        }
        return result;
    }

    // 거래전후잔여포인트 계산
    public CalcBefAfrBalDto getCalcBefAfrBalPt(EnumPoint.TrGb trGbCd, Long ptNo, Long reqPt){
        return ptMapper.selectPtMstCalcBefAfrBalPt(
                        new CalcBefAfrBalDto(trGbCd, ptNo, reqPt));
    }

    public Long getTotalBalPtMstList(Long memberNo, Long contNo){
        return ptMapper.selectBalPtSum(new PtSearch(memberNo, contNo)); // 해당 계약 전체의 미소멸/미사용 잔여포인트
    }
}
