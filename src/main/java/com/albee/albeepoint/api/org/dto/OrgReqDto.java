package com.albee.albeepoint.api.org.dto;

import com.albee.albeepoint.api.org.constant.EnumOrg;
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.mapper.base.t_org_mst.TOrgMst; 
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;


@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "기관 정보 등록/수정 요청 DTO")
public class OrgReqDto {
    @Schema(description = "기관코드", requiredMode = REQUIRED, maxLength = 100, example = "ORG0001")
    private String orgCd;

    @Schema(description = "기관명", requiredMode = REQUIRED, maxLength = 200, example = "홍삼나라")
    private String orgNm;

    @Schema(description = "기관노출명(미입력시 기관명과 동일)", requiredMode = NOT_REQUIRED, maxLength = 200, example = "홍삼나라")
    private String orgDispNm;

    @Schema(description = "적립가능여부(Y/N, 미입력시 Y)", requiredMode = NOT_REQUIRED, defaultValue = "Y", example = "Y")
    private String saveCanYn;

    @Schema(description = "사용가능여부(Y/N, 미입력시 Y)", requiredMode = NOT_REQUIRED, defaultValue = "Y", example = "Y")
    private String useCanYn;

    @Schema(description = "회원자동등록여부(Y/N, 미입력시 Y)", requiredMode = NOT_REQUIRED, defaultValue = "Y", example = "Y")
    private String memberAutoRegYn;

    @Schema(description = "기관상태코드(NORMAL:정상, STOP:중지)", requiredMode = NOT_REQUIRED, defaultValue = "NORMAL", example = "NORMAL")
    private EnumOrg.OrgSts orgStsCd;

    @JsonIgnore
    public TOrgMst getEntity(){
        TOrgMst entity = new TOrgMst();
        ComUtil.objectCopy(this, entity);
        return entity;
    }

    @JsonIgnore
    public OrgSearchDto getOrgSearch(){
        OrgSearchDto orgSearch = new OrgSearchDto();
        ComUtil.objectCopy(this, orgSearch);
        return orgSearch;
    }

}