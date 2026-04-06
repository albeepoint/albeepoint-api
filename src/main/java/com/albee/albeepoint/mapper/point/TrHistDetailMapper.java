package com.albee.albeepoint.mapper.point;

import org.springframework.stereotype.Component;
 
import com.albee.albeepoint.api.point.dto.TrHistDetailDto;
import com.albee.albeepoint.api.point.dto.TrHistDetailListRegReqDto;
import com.albee.albeepoint.api.point.dto.TrHistDetailSumDto;
import com.albee.albeepoint.api.point.dto.TrHistRegReqDto;
import com.albee.albeepoint.api.point.dto.TrHistSearchDto;

import java.util.List;

@Component
public interface TrHistDetailMapper {

    int insertIsuCancelMultiTrHistDetail(TrHistDetailListRegReqDto dom);

    int insertUseMultiTrHistDetail(TrHistDetailListRegReqDto dom);

    int insertUseCancelMultiTrHistDetail(TrHistDetailListRegReqDto dom);

    TrHistDetailDto selectTrHistDetail(TrHistSearchDto dom);

    TrHistDetailDto selectLastOneTrHistDetail(TrHistSearchDto dom);

    TrHistDetailDto selectBaseIsuDtForMileage(TrHistSearchDto dom);

    TrHistDetailSumDto selectTrHistDetailIsuSum(TrHistSearchDto dom);

    TrHistDetailSumDto selectTrHistDetailUseSum(TrHistSearchDto dom);

    // 특정 TrSno 에 대한 거래이력 조회
    List<TrHistDetailDto> selectTrHistDetailForTrTrace(TrHistSearchDto dom);

    Long selectTrHistDetailListTotalCnt(TrHistSearchDto dom);

    List<TrHistDetailDto> selectTrHistDetailList(TrHistSearchDto dom);
}
