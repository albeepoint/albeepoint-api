package com.albee.albeepoint.mapper.point;

import org.springframework.stereotype.Component;

import com.albee.albeepoint.api.point.dto.CalcBefAfrBalDto;
import com.albee.albeepoint.api.point.dto.PtMstDto;
import com.albee.albeepoint.api.point.dto.PtMstTargetDto;
import com.albee.albeepoint.api.point.dto.PtReqDto;
import com.albee.albeepoint.api.point.dto.PtSearchDto;
import com.albee.albeepoint.mapper.base.t_pt_mst.TPtMst;

import java.util.List;

@Component
public interface PtMstMapper{

    // 사용대상 조회
    List<PtMstTargetDto> selectUseTargetListByReqPt(PtSearchDto dom);

    // 사용취소대상 조회
    List<PtMstTargetDto> selectUseCancelTargetListByReqPt(PtSearchDto dom);

    /**
     * 발행취소/사용/사용취소 대상 조회
     * @param dom PtSearch
     * @return List<PtMstProcTargetDto>
     */
    List<PtMstTargetDto> selectBalPtTargetListByReqPt(PtSearchDto dom);

    int updateUsePtMst(PtReqDto dom);

    int updateUseCancelPtMst(PtReqDto dom);

    int updateIsuPtMst(PtReqDto dom);

    int updateIsuCancelPtMst(PtReqDto dom);


    // 일괄 사용 업데이트.
    int updateMultiUsePtMst(PtSearchDto dom);

    // 일괄 사용취소 업데이트.
    int updateMultiUseCancelPtMst(PtSearchDto dom);

    // 일괄 발행취소 업데이트
    int updateMultiIsuCancelPtMst(PtSearchDto dom);

    long selectSeqPtMstNo();

    int insertPtMst(TPtMst dom);

    PtMstDto selectPtMst(PtSearchDto dom);

    CalcBefAfrBalDto selectPtMstCalcBefAfrBalPt(CalcBefAfrBalDto dom);

    long selectBalPtSum(PtSearchDto dom);

    List<PtMstDto> selectMultiContBalPtSum(PtSearchDto dom);

    Long selectPtMstListTotalCnt(PtSearchDto dom);

    List<PtMstDto> selectPtMstList(PtSearchDto dom);
}
