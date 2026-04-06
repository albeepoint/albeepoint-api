package com.albee.albeepoint.api.point.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema; 
import lombok.Data; 

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;


@Data 
public class MemberContPtResDto {

    @Schema(description = "순번", requiredMode = REQUIRED, maxLength = 10, example = "1")
    private Long rowNum;

    @Schema(description = "계약번호", requiredMode = REQUIRED, maxLength = 10, example = "20001")
    private Long contNo;

    @Schema(description = "계약명", requiredMode = REQUIRED, maxLength = 100, example = "알비 포인트 서비스")
    private String contNm;

    @JsonIgnore
    @Schema(description = "회원포인트번호", requiredMode = REQUIRED, maxLength = 10, example = "1111")
    private Long memberPtNo;

    @JsonIgnore
    @Schema(description = "기관코드", requiredMode = REQUIRED, maxLength = 20, example = "ORG001")
    private String orgCd;

    @JsonIgnore
    @Schema(description = "회원ID", requiredMode = REQUIRED, maxLength = 100, example = "aaa001")
    private String memberId;

    @Schema(description = "사용가능포인트", requiredMode = REQUIRED, maxLength = 19, example = "10000")
    private Long ablePt;

    @Schema(description = "잔여포인트", requiredMode = REQUIRED, maxLength = 19, example = "10000")
    private Long balPt;

}
