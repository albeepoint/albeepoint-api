package com.albee.albeepoint.api.point.dto;
 
import com.albee.albeepoint.api.common.constant.AlbeeConst;
import com.albee.albeepoint.api.contract.dto.ContRealDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.sql.Timestamp;


@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PtMstTargetDto {
    private Long trSeq;

    private Long limitYn; // 1회사용제한 존재시 1, 없으면 0

    private Long memberPtNo;

    private Long ptNo;                // 포인트번호

    private Long contNo;              // 계약번호

    private String contNm;

    private ContRealDto contReal;

    @JsonFormat(pattern = AlbeeConst.timestampJsonPattern, timezone = AlbeeConst.defaultTimeZone)
    private Timestamp isuDt;

    @JsonFormat(pattern = AlbeeConst.timestampJsonPattern, timezone = AlbeeConst.defaultTimeZone)
    private Timestamp useEndDt;

    private Long orglTrSno;

    private Long ablePt;

    private Long balPt;

    private Long reqPt;

    private Long accBalPt;

    private Long isuPt;

    private Long isuCancelPt;

    private Long usePt;

    private Long useCancelPt;
}
