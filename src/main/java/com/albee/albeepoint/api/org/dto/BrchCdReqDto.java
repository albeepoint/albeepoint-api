package com.albee.albeepoint.api.org.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.mapper.base.t_brch_mst.TBrchMst;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "지점 조회 요청 DTO")
public class BrchCdReqDto {
    @Schema(description = "기관코드", requiredMode = REQUIRED, maxLength = 100, example = "ORG0001")
    private String orgCd;

    @Schema(description = "지점코드", requiredMode = REQUIRED, maxLength = 100, example = "BRCH0001")
    private String brchCd;


    @JsonIgnore
    public TBrchMst getEntity(){
        TBrchMst entity = new TBrchMst();
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