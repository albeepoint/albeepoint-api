package com.albee.albeepoint.api.contract.dto;
  
import io.swagger.v3.oas.annotations.media.Schema; 
import lombok.Data; 

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data 
@Schema(description = "계약정보 조회 VO")
public class ContNoReqDto {
    @Schema(description = "계약번호", requiredMode = REQUIRED, maxLength = 100, example = "20001")
    private Long contNo; 
}