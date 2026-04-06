package com.albeepoint.core.biz.cont.service;

import com.albeepoint.core.biz.cont.models.EnumCont;
import com.albeepoint.core.biz.cont.models.dto.ContSearch;
import com.albeepoint.core.biz.cont.models.entity.SubContMstEntity;
import com.albeepoint.core.biz.cont.models.ivo.request.SubContModReqIVo;
import com.albeepoint.core.biz.cont.models.ivo.request.SubContRegReqIVo;
import com.albeepoint.core.biz.cont.repository.SubContMstMapper;
import com.albeepoint.core.common.models.dto.ResultListDto;
import com.albeepoint.core.common.util.DateUtil;
import com.albeepoint.core.common.util.VdUtil;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

import static com.albeepoint.core.common.models.ErrorCode.*;

@RequiredArgsConstructor
@Service
@Transactional
public class SubContMstService {
    @Autowired
    SubContMstMapper subContMstMapper;
    @Autowired
    SubContMstHistService subContMstHistService;
    @Autowired
    ContMstService contMstService;

    public static final Logger log = LogManager.getLogger(SubContMstService.class);

    public SubContMstEntity regSubContMst(SubContRegReqIVo dom) {
        VdUtil.emptyEc(dom.getContNo(), BIZ_ERR_001005, "계약번호 필수");
        ContSearch contSearch = new ContSearch(dom.getContNo());
        VdUtil.emptyEc(contMstService.getContMst(contSearch) , BIZ_ERR_001009, "계약정보 없음. 하위계약등록시에는 원계약번호 존재해야 함");

        SubContMstEntity subContMstEntity = checkSubContMst(dom.getEntity());

        // 계약일련번호 채번
        Long contSeq = subContMstMapper.selectSeqSubContMstNo(contSearch);
        subContMstEntity.setContSeq(contSeq);

        VdUtil.ec(subContMstMapper.insertSubContMst(subContMstEntity) <= 0
                , BIZ_ERR_001085, "서브계약 인서트 오류");

        // 서브계약이력 등록
        subContMstHistService.regSubContMstHist(subContMstEntity);

        return subContMstMapper.selectSubContMst(new ContSearch(subContMstEntity));
    }

    public SubContMstEntity modSubContMst(SubContModReqIVo dom) {
        VdUtil.emptyEc(dom.getContNo(), BIZ_ERR_001005, "계약번호 필수");
        VdUtil.emptyEc(dom.getContSeq(), BIZ_ERR_001083, "계약일련번호 필수");

        ContSearch contSearch = new ContSearch(dom.getContNo(), dom.getContSeq());

        getSubContMstWec(contSearch);

        SubContMstEntity subContMstentity = checkSubContMst(dom.getEntity());

        VdUtil.ec(subContMstMapper.updateSubContMst(subContMstentity)<= 0
                , BIZ_ERR_001085, "하위계약 업데이트 오류");

        // 서브계약이력 등록
        subContMstHistService.regSubContMstHist(subContMstentity);

        return subContMstMapper.selectSubContMst(contSearch);
    }

    public SubContMstEntity getSubContMst(ContSearch dom) {
        return subContMstMapper.selectSubContMst(dom);
    }

    public SubContMstEntity getSubContMstWec(ContSearch dom) {
        return (SubContMstEntity)VdUtil.emptyEc(subContMstMapper.selectSubContMst(dom), BIZ_ERR_001084, "하위계약 미존재");
    }

    public ResultListDto<SubContMstEntity> getSubContMstList(ContSearch dom) {
        ResultListDto<SubContMstEntity> result = new ResultListDto<>();
        Long totalCnt = subContMstMapper.selectSubContMstListTotalCnt(dom);
        result.setTotalCnt(totalCnt != null ? totalCnt : 0L);
        if(result.getTotalCnt() > 0){
            List<SubContMstEntity> list = subContMstMapper.selectSubContMstList(dom);
            result.setList(list);
            result.setPageCnt(list != null && list.size() > 0 ? list.size() : 0);
        }
        return result;
    }

    private SubContMstEntity checkSubContMst(SubContMstEntity dom){

        if(VdUtil.isEmpty(dom.getStartDt())){
            dom.setStartDt(Timestamp.valueOf(DateUtil.convStringToLocalDateTime(DateUtil.getTodayString(), "000000000000000")));
        }

        if(VdUtil.isEmpty(dom.getEndDt())){
            // MySql Timestamp 는 1970년 ~ 2038년 1월 까지만 설정 가능함. 그래서 그냥 null 로 두기로 함
            // dom.setEndDt(Timestamp.valueOf(DateUtil.convStringToLocalDateTime("20380101", "000000000000000")));
            dom.setEndDt(null);
        }

        if(VdUtil.isEmpty(dom.getMixUseYn())) dom.setMixUseYn("N");

        if(VdUtil.isEmpty(dom.getIsuStartDt())) dom.setIsuStartDt(dom.getStartDt());
        if(VdUtil.isEmpty(dom.getIsuEndDt())) dom.setIsuEndDt(dom.getEndDt());

        if(VdUtil.isEmpty(dom.getUseStartDt())) dom.setUseStartDt(dom.getStartDt());
        if(VdUtil.isEmpty(dom.getUseEndDt())) dom.setUseEndDt(dom.getEndDt());

        if(VdUtil.isEmpty(dom.getIsuPeriodLimitTypeCd())) dom.setIsuPeriodLimitTypeCd(EnumCont.IsuPeriodLimitType.NONE);
        if(VdUtil.isEmpty(dom.getUsePeriodLimitTypeCd())) dom.setUsePeriodLimitTypeCd(EnumCont.UsePeriodLimitType.NONE);

        if(VdUtil.isEmpty(dom.getUseStartDtCalcTypeCd())) dom.setUseStartDtCalcTypeCd(EnumCont.UseStartDtCalcType.NONE);
        if(VdUtil.isEmpty(dom.getUseStartDtCalcVal())) dom.setUseStartDtCalcVal(0);

        if(VdUtil.isEmpty(dom.getUsePeriodCalcTypeCd())) dom.setUsePeriodCalcTypeCd(EnumCont.UsePeriodCalcType.NONE);
        if(VdUtil.isEmpty(dom.getUsePeriodCalcVal())) dom.setUsePeriodCalcVal(0);

        if(VdUtil.isEmpty(dom.getOnceIsuLimitTypeCd())) dom.setOnceIsuLimitTypeCd(EnumCont.OnceIsuLimitType.NONE);

        if(VdUtil.isEmpty(dom.getTotalIsuLimitTypeCd())) dom.setTotalIsuLimitTypeCd(EnumCont.TotalIsuLimitType.NONE);

        if(VdUtil.isEmpty(dom.getContStsCd())) dom.setContStsCd(EnumCont.ContSts.NORMAL);

        VdUtil.ec(dom.getEndDt().before(dom.getStartDt()), BIZ_ERR_001023, "계약 시작/종료일 확인 필요");
        VdUtil.ec(dom.getIsuEndDt().before(dom.getIsuStartDt()), BIZ_ERR_001024, "발행 시작/종료일 확인 필요");
        VdUtil.ec(dom.getUseEndDt().before(dom.getUseEndDt()), BIZ_ERR_001026, "사용 시작/종료일 확인 필요");

        return dom;
    }

}
