package com.albee.albeepoint.api.point.dto;
 
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;


@Data 
@Schema(description = "적립 취소 요청 VO")
public class IsuCancelReqDto {

    @Schema(description = "계약번호", requiredMode = NOT_REQUIRED, maxLength = 10, example = "20001")
    private Long contNo;

    @Schema(description = "회원ID", requiredMode = REQUIRED, maxLength = 100, example = "aaa001")
    private String memberId;

    @Schema(description = "기관코드", requiredMode = REQUIRED, maxLength = 20, example = "ORG001")
    private String orgCd;

    @Schema(description = "지점코드", requiredMode = NOT_REQUIRED, maxLength = 100, example = "BRCH0001")
    private String brchCd;

    @Schema(description = "원거래번호", requiredMode = NOT_REQUIRED, maxLength = 19, example = "1025")
    private Long orglTrSno;

    @Schema(description = "적립취요청포인트", requiredMode = NOT_REQUIRED, maxLength = 19, example = "10000")
    private Long isuCancelReqPt;

    @JsonIgnore
    public IsuCancelReqDto getIsuCancelReqDto(){
        IsuCancelReqDto isuCancelReqDto = new IsuCancelReqDto();
        isuCancelReqDto.setOrgCd(this.orgCd);
        isuCancelReqDto.setBrchCd(this.brchCd);
        isuCancelReqDto.setContNo(this.contNo);
        isuCancelReqDto.setMemberId(this.memberId);
        isuCancelReqDto.setOrglTrSno(this.orglTrSno);
        isuCancelReqDto.setIsuCancelReqPt(this.isuCancelReqPt);
        return isuCancelReqDto;
    } 
}
