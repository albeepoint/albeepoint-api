package com.albee.albeepoint.api.contract.service;

import com.albee.albeepoint.api.common.constant.ErrorCode;
import com.albee.albeepoint.api.common.dto.ResultListDto;
import com.albee.albeepoint.api.contract.constant.EnumCont;
import com.albee.albeepoint.api.contract.dto.ContModReqDto;
import com.albee.albeepoint.api.contract.dto.ContRegReqDto;
import com.albee.albeepoint.api.contract.dto.ContSearchDto;
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.api.util.DateUtil;
import com.albee.albeepoint.api.util.VdUtil;
import com.albee.albeepoint.mapper.base.t_cont_mst.TContMst;
import com.albee.albeepoint.mapper.base.t_cont_mst.TContMstMapper; 
import com.albee.albeepoint.mapper.contract.ContractMstMapper;

import lombok.extern.log4j.Log4j2;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.util.List;
 
@Log4j2
@Service
@Transactional
public class ContMstService{
    @Autowired
    private TContMstMapper tContMstMapper;

    @Autowired
    private ContractMstMapper contMstMapper;

    @Autowired
    ContMstHistService contMstHistService;
 
    public void regContMst(ContRegReqDto dom) {
        TContMst tContMst = (TContMst)ComUtil.objectCopy(dom, TContMst.class);
        tContMst = checkContMst(tContMst); 

        // 계약등록
        VdUtil.ec(this.tContMstMapper.insert(tContMst) < 1, ErrorCode.BIZ_ERR_001086, "계약 인서트 오류"); 

        // 계약이력 등록
        this.contMstHistService.regContMstHist(tContMst);
    }

    public void modContMst(ContModReqDto dom) {
        VdUtil.emptyEc(dom.getContNo(), ErrorCode.BIZ_ERR_001005, "계약번호 필수 입력");
        this.getContMstWec(dom.getContNo());  // 존재하는 계약여부 체크

        TContMst tContMst = (TContMst)ComUtil.objectCopy(dom, TContMst.class);

        // 계약정보 업데이트
        VdUtil.ec(this.tContMstMapper.updateByPrimaryKeySelective(tContMst) <= 0, ErrorCode.BIZ_ERR_001086, "계약 업데이트 오류");

        // 계약이력 등록
        this.contMstHistService.regContMstHist(tContMst);
    }

    public TContMst getContMstWec(long contNo){ 
        TContMst tContMst = this.tContMstMapper.selectByPrimaryKey(contNo);
        VdUtil.emptyEc(tContMst, ErrorCode.BIZ_ERR_001009, "계약정보 없음");
        return tContMst;
    }

    public ResultListDto<TContMst> getContMstList(ContSearchDto dom) {
        ResultListDto<TContMst> result = new ResultListDto<>();
        Long totalCnt = this.contMstMapper.selectContMstListTotalCnt(dom);
        result.setTotalCnt(totalCnt != null ? totalCnt : 0L);
        if(result.getTotalCnt() > 0){
            List<TContMst> list = contMstMapper.selectContMstList(dom);
            result.setList(list);
            result.setPageCnt(list != null && list.size() > 0 ? list.size() : 0);
        }
        return result;
    }

    private TContMst checkContMst(TContMst dom){
        if(VdUtil.isEmpty(dom.getStartDt())){
            dom.setStartDt(DateUtil.convStringToLocalDateTime(DateUtil.getTodayString(), "000000000000000"));
        }

        if(VdUtil.isEmpty(dom.getEndDt())){
            // MySql Timestamp 는 1970년 ~ 2038년 1월 까지만 설정 가능함. 그래서 그냥 null 로 두기로 함
            // dom.setEndDt(Timestamp.valueOf(DateUtil.convStringToLocalDateTime("20380101", "000000000000000")));
            dom.setEndDt(null);
        }

        if(VdUtil.isEmpty(dom.getMixUseYn())) dom.setMixUseYn("N");

        if(VdUtil.isEqual(dom.getPtTypeCd(), EnumCont.PtType.MILEAGE)){
            // MILEAGE 이면 기간과 관련한 아무런 제한 없는 계약임
            dom.setIsuStartDt(dom.getStartDt());
            dom.setIsuEndDt(dom.getEndDt());
            dom.setUseStartDt(dom.getStartDt());
            dom.setUseEndDt(dom.getEndDt());
            dom.setIsuPeriodLimitTypeCd(EnumCont.IsuPeriodLimitType.NONE.getCode());
            dom.setIsuPeriodMaxPt(null);
            dom.setIsuPeriodMaxCnt(null);
            dom.setUsePeriodLimitTypeCd(EnumCont.UsePeriodLimitType.NONE.getCode());
            dom.setUsePeriodMaxPt(null);
            dom.setUsePeriodMaxCnt(null);
            dom.setUseStartDtCalcTypeCd(EnumCont.UseStartDtCalcType.NONE.getCode());
            dom.setUseStartDtCalcVal(null);
            dom.setUsePeriodCalcTypeCd(EnumCont.UsePeriodCalcType.NONE.getCode());
            dom.setUsePeriodCalcVal(null);
        }else{
            // COUPON 이면 각종 제한 가능한 계약임
            if (VdUtil.isEmpty(dom.getIsuStartDt())) dom.setIsuStartDt(dom.getStartDt());
            if (VdUtil.isEmpty(dom.getIsuEndDt())) dom.setIsuEndDt(dom.getEndDt());

            if (VdUtil.isEmpty(dom.getUseStartDt())) dom.setUseStartDt(dom.getStartDt());
            if (VdUtil.isEmpty(dom.getUseEndDt())) dom.setUseEndDt(dom.getEndDt());

            if (VdUtil.isEmpty(dom.getIsuPeriodLimitTypeCd()))
                dom.setIsuPeriodLimitTypeCd(EnumCont.IsuPeriodLimitType.NONE.getCode());
            if (VdUtil.isEmpty(dom.getUsePeriodLimitTypeCd()))
                dom.setUsePeriodLimitTypeCd(EnumCont.UsePeriodLimitType.NONE.getCode());

            if (VdUtil.isEmpty(dom.getUseStartDtCalcTypeCd()))
                dom.setUseStartDtCalcTypeCd(EnumCont.UseStartDtCalcType.NONE.getCode());
            if (VdUtil.isEmpty(dom.getUseStartDtCalcVal())) dom.setUseStartDtCalcVal(0);

            if (VdUtil.isEmpty(dom.getUsePeriodCalcTypeCd()))
                dom.setUsePeriodCalcTypeCd(EnumCont.UsePeriodCalcType.NONE.getCode());
            if (VdUtil.isEmpty(dom.getUsePeriodCalcVal())) dom.setUsePeriodCalcVal(0);

            if (VdUtil.isEmpty(dom.getOnceIsuLimitTypeCd())) dom.setOnceIsuLimitTypeCd(EnumCont.OnceIsuLimitType.NONE.getCode());
            if (VdUtil.isEmpty(dom.getOnceUseLimitTypeCd())) dom.setOnceUseLimitTypeCd(EnumCont.OnceUseLimitType.NONE.getCode());

            if (VdUtil.isEmpty(dom.getTotalIsuLimitTypeCd()))
                dom.setTotalIsuLimitTypeCd(EnumCont.TotalIsuLimitType.NONE.getCode());


            VdUtil.ec(dom.getEndDt().isBefore(dom.getStartDt()), ErrorCode.BIZ_ERR_001023, "계약 시작/종료일 확인 필요");
            VdUtil.ec(dom.getIsuEndDt().isBefore(dom.getIsuStartDt()), ErrorCode.BIZ_ERR_001024, "발행 시작/종료일 확인 필요");
            VdUtil.ec(dom.getUseEndDt().isBefore(dom.getUseEndDt()), ErrorCode.BIZ_ERR_001026, "사용 시작/종료일 확인 필요");
        }

        if (VdUtil.isEmpty(dom.getContStsCd())) dom.setContStsCd(EnumCont.ContSts.NORMAL.getCode());

        if (VdUtil.isEqual(dom.getOnceIsuLimitTypeCd(), EnumCont.OnceIsuLimitType.FIX)) {
            VdUtil.emptyEc(dom.getOnceIsuFixPt(), ErrorCode.BIZ_ERR_001061, "계약정보 1회 고정발행포인트 필수 입력");
        } else if (VdUtil.isEqual(dom.getOnceIsuLimitTypeCd(), EnumCont.OnceIsuLimitType.MIN)) {
            VdUtil.emptyEc(dom.getOnceIsuMinPt(), ErrorCode.BIZ_ERR_001062, "계약정보 1회 최소발행포인트 필수 입력");
        } else if (VdUtil.isEqual(dom.getOnceIsuLimitTypeCd(), EnumCont.OnceIsuLimitType.MAX)) {
            VdUtil.emptyEc(dom.getOnceIsuMaxPt(), ErrorCode.BIZ_ERR_001064, "계약정보 1회 최대발행포인트 필수 입력");
        } else if (VdUtil.isEqual(dom.getOnceIsuLimitTypeCd(), EnumCont.OnceIsuLimitType.BOTH)) {
            VdUtil.emptyEc(dom.getOnceIsuMinPt(), ErrorCode.BIZ_ERR_001062, "계약정보 1회 최소발행포인트 필수 입력");
            VdUtil.emptyEc(dom.getOnceIsuMaxPt(), ErrorCode.BIZ_ERR_001064, "계약정보 1회 최대발행포인트 필수 입력");
            VdUtil.ec(dom.getOnceIsuMinPt() > dom.getOnceIsuMaxPt(), ErrorCode.BIZ_ERR_001098, "1회최소사용포인트는 1회최대사용포인트보가 클 수 없음");
        } else if (VdUtil.isEqual(dom.getOnceIsuLimitTypeCd(), EnumCont.OnceIsuLimitType.RATE)) {
            VdUtil.emptyEc(dom.getOnceIsuPurchaseRate(), ErrorCode.BIZ_ERR_001066, "계약정보 1회 구매금액발행비율 필수 입력");
        }

        return dom;
    }

}
