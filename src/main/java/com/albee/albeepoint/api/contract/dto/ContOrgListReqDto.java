package com.albee.albeepoint.api.contract.dto;
 
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "계약 기관 목록 조회 VO")
public class ContOrgListReqDto {

    @Schema(description = "계약번호", requiredMode = REQUIRED, maxLength = 10, example = "20001")
    private Long contNo;    // 계약번호

    @Schema(description = "계약순번(기관 특화 계약 정보 존재시 해당 계약순번 기재, 메인 계약과 동일하면 미입력)", requiredMode = NOT_REQUIRED, maxLength = 10, example = "1")
    private Long contSeq;    // 계약순번

    @Schema(description = "기관코드", requiredMode = REQUIRED, maxLength = 20, example = "ORG001")
    private String orgCd;

    @Schema(description = "조회 시작 인덱스", requiredMode = NOT_REQUIRED, example = "51")
    private Integer startIndex;

    @Schema(description = "조회 종료 인덱스", requiredMode = NOT_REQUIRED, example = "100")
    private Integer endIndex; 
}