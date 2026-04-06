package com.albee.albeepoint.api.contract.dto;
 
import com.albee.albeepoint.api.contract.constant.EnumContRel; 
import io.swagger.v3.oas.annotations.media.Schema; 
import lombok.Data; 

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data 
@Schema(description = "계약 관계 목록 조회 요청 DTO")
public class ContRelListReqDto {

    @Schema(description = "계약번호", requiredMode = REQUIRED, maxLength = 10, example = "20001")
    private Long contNo;    // 계약번호

    @Schema(description = "계약관계유형코드[USE_AFT_ADD_ISU:사용 후 추가적립, EXCLUSIVE_USE:배타적 사용, EXCLUSIVE_ISU:배타적 적립", requiredMode = REQUIRED, maxLength = 20, example = "USE_AFT_ADD_ISU")
    private EnumContRel.ContRel ContRelGbCd;    //  계약관계유형코드

    @Schema(description = "계약역할", requiredMode = NOT_REQUIRED, maxLength = 20, example = "PARENT")
    private String contRole;

    @Schema(description = "조회 시작 인덱스", requiredMode = NOT_REQUIRED, example = "51")
    private Integer startIndex;

    @Schema(description = "조회 종료 인덱스", requiredMode = NOT_REQUIRED, example = "100")
    private Integer endIndex; 
}