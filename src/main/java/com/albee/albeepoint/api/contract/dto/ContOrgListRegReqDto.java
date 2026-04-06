package com.albee.albeepoint.api.contract.dto;
 
import io.swagger.v3.oas.annotations.media.Schema; 
import lombok.Data; 
 
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data 
@Schema(description = "계약기관 목록 등록 VO")
public class ContOrgListRegReqDto {
    @Schema(description = "계약번호", requiredMode = REQUIRED, maxLength = 100, example = "20001")
    private Long contNo;

    @Schema(description = "계약기관 목록", requiredMode = REQUIRED)
    private List<ContOrgRegReqDto> contOrgList;
 
}