package com.albee.albeepoint.api.point.dto;
  
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;


@Data  
@Schema(description = "포인트 적립 요청 VO")
public class IsuReqDto {
    @Schema(description = "기관코드", requiredMode = REQUIRED, maxLength = 20, example = "ORG001")
    private String orgCd;

    @Schema(description = "지점코드", requiredMode = NOT_REQUIRED, maxLength = 100, example = "BRCH0001")
    private String brchCd;

    @Schema(description = "계약번호", requiredMode = NOT_REQUIRED, maxLength = 10, example = "20001")
    private Long contNo;

    @Schema(description = "회원ID", requiredMode = REQUIRED, maxLength = 100, example = "aaa001")
    private String memberId;

    @Schema(description = "회원명", requiredMode = REQUIRED, maxLength = 100, example = "aaa001")
    private String memberNm;

    @Schema(description = "적립요청포인트", requiredMode = NOT_REQUIRED, maxLength = 19, example = "10000")
    private Long isuReqPt;

    @Schema(description = "구매금액", requiredMode = NOT_REQUIRED, maxLength = 19, example = "10000")
    private Long purchaseAmt;

    @Schema(description = "적립정보", requiredMode = NOT_REQUIRED, maxLength = 20, example = "USE_ADD_ISU")
    private String isuInfo;

    @Schema(description = "원거래", requiredMode = NOT_REQUIRED, maxLength = 19, example = "10000")
    private Long orglTrSno; 
}
