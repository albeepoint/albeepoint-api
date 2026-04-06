package com.albee.albeepoint.api.contract.dto;
 
import com.albee.albeepoint.api.contract.constant.EnumCont;
import com.albee.albeepoint.api.org.constant.EnumOrg; 
import io.swagger.v3.oas.annotations.media.Schema; 
import lombok.Data; 

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data 
@Schema(description = "계약 기관 등록/수정 요청 DTO")
public class ContOrgRegReqDto {
    @Schema(description = "계약순번(기관 특화 계약 정보 존재시 해당 계약순번 기재, 메인 계약과 동일하면 미입력)", requiredMode = NOT_REQUIRED, maxLength = 10, example = "1")
    private Long contSeq;    // 계약순번
        
    @Schema(description = "기관코드", requiredMode = REQUIRED, maxLength = 20, example = "ORG001")
    private String orgCd;

    @Schema(description = "지점정책유형코드[ALL:전체, EXCLUDE_ONLY:기명지점제외 모두 해당, INCLUDE_ONLY:기명지점만 해당]", requiredMode = REQUIRED, maxLength = 20, example = "ALL")
    private EnumOrg.BrchPolicyType brchPolicyTypeCd;    //  지점정책유형코드(ALL:전체, EXCLUDE_ONLY:기명지점제외 모두 해당, INCLUDE_ONLY:기명지점만 해당)

    @Schema(description = "적립가능여부(Y/N)", requiredMode = REQUIRED, maxLength = 1, example = "Y")
    private String saveCanYn;

    @Schema(description = "사용가능여부(Y/N)", requiredMode = REQUIRED, maxLength = 1, example = "Y")
    private String useCanYn;

    @Schema(description = "계약기관상태코드[NORMAL:정상, STOP:중지]", requiredMode = NOT_REQUIRED, defaultValue = "NORMAL", maxLength = 20, example = "NORMAL")
    private EnumCont.ContOrgSts contOrgStsCd;

    @Schema(description = "총 적립 가능 포인트", requiredMode = NOT_REQUIRED, maxLength = 10, example = "100000000")
    private Long totalIsuAblePt;

    @Schema(description = "총 적립 가능 건수", requiredMode = NOT_REQUIRED, maxLength = 10, example = "10000")
    private Long totalIsuAbleCnt; 
}
