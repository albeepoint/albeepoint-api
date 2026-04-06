package com.albee.albeepoint.api.contract.dto;
  
import io.swagger.v3.oas.annotations.media.Schema; 
import lombok.Data; 

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;


@Data 
@Schema(description = "하위 계약정보 수정 VO")
public class SubContListReqDto {
    @Schema(description = "계약번호", requiredMode = REQUIRED, maxLength = 10, example = "20001")
    private Long contNo;    // 계약번호

    @Schema(description = "계약순번(기관 특화 계약 정보 존재시 해당 계약순번 기재, 메인 계약과 동일하면 미입력)", requiredMode = NOT_REQUIRED, maxLength = 10, example = "1")
    private Long contSeq;    // 계약순번

    @Schema(description = "조회 시작 인덱스", requiredMode = NOT_REQUIRED, example = "51")
    private Integer startIndex;

    @Schema(description = "조회 종료 인덱스", requiredMode = NOT_REQUIRED, example = "100")
    private Integer endIndex;
 
}