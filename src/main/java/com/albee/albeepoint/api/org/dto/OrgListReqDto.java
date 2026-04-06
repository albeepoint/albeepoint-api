package com.albee.albeepoint.api.org.dto;
 
import com.albee.albeepoint.api.util.ComUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;


@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "기관 목록 조회 요청 DTO")
public class OrgListReqDto {
    @Schema(description = "기관코드", requiredMode = REQUIRED, maxLength = 100, example = "ORG0001")
    private String orgCd;

    @Schema(description = "기관명", requiredMode = REQUIRED, maxLength = 200, example = "홍삼나라")
    private String orgNm;

    @Schema(description = "조회 시작 인덱스", requiredMode = NOT_REQUIRED, example = "51")
    private Integer startIndex;

    @Schema(description = "조회 종료 인덱스", requiredMode = NOT_REQUIRED, example = "100")
    private Integer endIndex;

    @JsonIgnore
    public OrgSearchDto getOrgSearch(){
        OrgSearchDto orgSearch = new OrgSearchDto();
        ComUtil.objectCopy(this, orgSearch);
        return orgSearch;
    }

}