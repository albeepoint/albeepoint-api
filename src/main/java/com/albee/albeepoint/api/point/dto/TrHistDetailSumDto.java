package com.albee.albeepoint.api.point.dto;

import lombok.*;


@Data 
public class TrHistDetailSumDto {
    private Long isuSum;

    private Long isuCancelSum;

    private Long useSum;

    private Long useCancelSum;

    private Long expSum;

    private Long isuCnt;

    private Long isuCancelCnt;

    private Long useCnt;

    private Long useCancelCnt;

    private Long expCnt;

    private Long netIsuCntSum;

    private Long netIsuPtSum;

    private Long netUseCntSum;

    private Long netUsePtSum;
}

