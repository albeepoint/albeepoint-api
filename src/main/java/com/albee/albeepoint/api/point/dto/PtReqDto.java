package com.albee.albeepoint.api.point.dto;
 
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;


@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class PtReqDto {
    @Schema(description = "포인트번호", requiredMode = NOT_REQUIRED, maxLength = 19, example = "10000")
    private Long ptNo;

    @Schema(description = "적립취요청포인트", requiredMode = NOT_REQUIRED, maxLength = 19, example = "10000")
    private Long reqPt;


    public PtReqDto(Long ptNo, Long reqPt) {
        this.ptNo = ptNo;
        this.reqPt = reqPt;
    }
 
}
