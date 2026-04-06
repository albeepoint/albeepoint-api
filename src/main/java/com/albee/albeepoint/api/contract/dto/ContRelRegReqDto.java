package com.albee.albeepoint.api.contract.dto;
 
import com.albee.albeepoint.api.contract.constant.EnumContRel; 
import io.swagger.v3.oas.annotations.media.Schema; 
import lombok.Data; 

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data 
@Schema(description = "계약 관계 등록 VO")
public class ContRelRegReqDto {
    @Schema(description = "계약관계번호", requiredMode = REQUIRED, maxLength = 10, example = "1")
    private Long contRelNo;    // 계약관계번호

    @Schema(description = "계약관계일련번호", requiredMode = REQUIRED, maxLength = 10, example = "1")
    private Long contRelSeq;    // 계약관계일련번호

    @Schema(description = "계약관계유형코드[USE_AFT_ADD_ISU:사용 후 추가적립, EXCLUSIVE_USE:배타적 사용, EXCLUSIVE_ISU:배타적 적립", requiredMode = REQUIRED, maxLength = 20, example = "USE_AFT_ADD_ISU")
    private EnumContRel.ContRel ContRelGbCd;    //  계약관계유형코드

    @Schema(description = "계약역할", requiredMode = NOT_REQUIRED, maxLength = 20, example = "PARENT")
    private String contRole;

    @Schema(description = "계약번호", requiredMode = NOT_REQUIRED, maxLength = 100, example = "20001")
    private Long contNo;

    @Schema(description = "연계계약번호", requiredMode = NOT_REQUIRED, maxLength = 100, example = "20001")
    private Long LinkContNo;

    @Schema(description = "계약관계정보", requiredMode = NOT_REQUIRED, maxLength = 1, example = "1")
    private String contRelInfo;

    @Schema(description = "계약관계사용여부(Y/N)", requiredMode = REQUIRED, maxLength = 1, example = "Y")
    private String contRelUseYn;
 
}
