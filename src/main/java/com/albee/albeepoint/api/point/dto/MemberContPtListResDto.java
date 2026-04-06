package com.albee.albeepoint.api.point.dto;
 
import io.swagger.v3.oas.annotations.media.Schema; 
import lombok.Data; 

import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;
 
@Data 
public class MemberContPtListResDto {

    @Schema(description = "기관코드", requiredMode = REQUIRED, maxLength = 20, example = "ORG001")
    private String orgCd;

    @Schema(description = "회원번호", requiredMode = REQUIRED, maxLength = 100, example = "111")
    private Long memberNo;

    @Schema(description = "회원ID", requiredMode = REQUIRED, maxLength = 100, example = "aaa001")
    private String memberId;

    @Schema(description = "총사용가능포인트", requiredMode = REQUIRED, maxLength = 19, example = "10000")
    private Long totalAblePt;

    @Schema(description = "총잔여포인트", requiredMode = REQUIRED, maxLength = 19, example = "10000")
    private Long totalBalPt;

    @Schema(description = "총보유계약수", requiredMode = REQUIRED, maxLength = 19, example = "4")
    private Long totalContCnt;

    private List<MemberContPtResDto> list;

}
