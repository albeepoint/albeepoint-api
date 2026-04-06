package com.albee.albeepoint.api.contract.dto;
 
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
@Schema(description = "계약 지점 조회 VO")
public class ContBrchReqDto {
    @Schema(description = "계약번호", requiredMode = REQUIRED, maxLength = 10, example = "20001")
    private Long contNo;    // 계약번호

    @Schema(description = "계약순번(기관 특화 계약 정보 존재시 해당 계약순번 기재, 메인 계약과 동일하면 미입력)", requiredMode = NOT_REQUIRED, maxLength = 10, example = "1")
    private Long contSeq;    // 계약순번
        
    @Schema(description = "기관코드", requiredMode = REQUIRED, maxLength = 20, example = "ORG001")
    private String orgCd;

    @Schema(description = "지점코드", requiredMode = REQUIRED, maxLength = 20, example = "BRCH0001")
    private String brchCd;


    @JsonIgnore
    public ContOrgSearchDto getContOrgSearch(){
        ContOrgSearchDto contOrgSearch = new ContOrgSearchDto();
        ComUtil.objectCopy(this, contOrgSearch);
        return contOrgSearch;
    }

    @JsonIgnore
    public ContBrchReqDto getIVo(){
        return (ContBrchReqDto) ComUtil.votoivoCopy(this);
    }
}
