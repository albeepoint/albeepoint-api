package com.albee.albeepoint.api.contract.dto;
  
import io.swagger.v3.oas.annotations.media.Schema; 
import lombok.Data; 
 
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data 
@Schema(description = "계약 관계 목록 등록 요청 DTO")
public class ContRelListRegReqDto {

    @Schema(description = "계약 관계 목록", requiredMode = REQUIRED)
    private List<ContRelRegReqDto> contRelList; 
}