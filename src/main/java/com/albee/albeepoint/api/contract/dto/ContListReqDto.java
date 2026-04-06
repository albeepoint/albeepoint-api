package com.albee.albeepoint.api.contract.dto;
 
import com.albee.albeepoint.api.util.ComUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "계약정보 목록 조회 VO")
public class ContListReqDto {
    @Schema(description = "계약번호", requiredMode = NOT_REQUIRED, maxLength = 100, example = "20001")
    private Long contNo;

    @Schema(description = "계약명", requiredMode = NOT_REQUIRED, maxLength = 100, example = "알비 포인트 서비스")
    private String contNm;

    @Schema(description = "기관코드", requiredMode = NOT_REQUIRED, maxLength = 100, example = "ORG0001")
    private String orgCd;

    @Schema(description = "조회 시작 인덱스", requiredMode = NOT_REQUIRED, example = "51")
    private Integer startIndex;

    @Schema(description = "조회 종료 인덱스", requiredMode = NOT_REQUIRED, example = "100")
    private Integer endIndex;

    @JsonIgnore
    public ContSearchDto getContSearch(){
        ContSearchDto contSearch = new ContSearchDto();
        ComUtil.objectCopy(this, contSearch);
        return contSearch;
    }

    @JsonIgnore
    public ContListReqDto getIVo(){
        return (ContListReqDto) ComUtil.votoivoCopy(this);
    }
}