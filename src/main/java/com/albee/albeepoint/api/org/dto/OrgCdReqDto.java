package com.albee.albeepoint.api.org.dto;
 
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.mapper.base.t_org_mst.TOrgMst;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;


@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "기관 조회(By기관코드) 요청 DTO")
public class OrgCdReqDto {
    @Schema(description = "기관코드", requiredMode = REQUIRED, maxLength = 100, example = "ORG0001")
    private String orgCd;

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