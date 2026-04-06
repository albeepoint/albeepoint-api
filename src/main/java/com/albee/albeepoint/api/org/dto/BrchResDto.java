package com.albee.albeepoint.api.org.dto;
 
import com.albee.albeepoint.api.org.constant.EnumOrg; 
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema; 
import lombok.Data; 

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;
 
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "지점 정보 응답 DTO")
public class BrchResDto {

    @Schema(description = "기관코드", requiredMode = REQUIRED, maxLength = 100, example = "ORG0001")
    private String orgCd;

    @Schema(description = "지점코드", requiredMode = REQUIRED, maxLength = 100, example = "BRCH0001")
    private String brchCd;

    @Schema(description = "지점명", requiredMode = REQUIRED, maxLength = 200, example = "강남지점")
    private String brchNm;

    @Schema(description = "기지점노출명(미입력시 지점명과 동일)", requiredMode = NOT_REQUIRED, maxLength = 200, example = "강남지점")
    private String brchDispNm;

    @Schema(description = "적립가능여부(Y/N, 미입력시 Y)", requiredMode = NOT_REQUIRED, defaultValue = "Y", example = "Y")
    private String saveCanYn;

    @Schema(description = "사용가능여부(Y/N, 미입력시 Y)", requiredMode = NOT_REQUIRED, defaultValue = "Y", example = "Y")
    private String useCanYn;

    @Schema(description = "회원자동등록여부(Y/N, 미입력시 Y)", requiredMode = NOT_REQUIRED, defaultValue = "Y", example = "Y")
    private String memberAutoRegYn;

    @Schema(description = "기관상태코드(NORMAL:정상, STOP:중지)", requiredMode = NOT_REQUIRED, defaultValue = "NORMAL", example = "NORMAL")
    private EnumOrg.OrgSts orgStsCd;

}