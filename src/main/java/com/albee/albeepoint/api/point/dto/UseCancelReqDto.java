package com.albee.albeepoint.api.point.dto;
 
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema; 
import lombok.Data; 

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;


@Data 
@Schema(description = "포인트 사용 요청 취소 VO")
public class UseCancelReqDto {
    @Schema(description = "기관코드", requiredMode = REQUIRED, maxLength = 20, example = "ORG001")
    private String orgCd;

    @Schema(description = "지점코드", requiredMode = NOT_REQUIRED, maxLength = 100, example = "BRCH0001")
    private String brchCd;

    @Schema(description = "계약번호", requiredMode = NOT_REQUIRED, maxLength = 10, example = "20001")
    private Long contNo;

    @Schema(description = "회원ID", requiredMode = REQUIRED, maxLength = 100, example = "aaa001")
    private String memberId;

    @Schema(description = "원거래번호", requiredMode = NOT_REQUIRED, maxLength = 19, example = "1025")
    private Long orglTrSno;

    @Schema(description = "사용취소요청포인트", requiredMode = REQUIRED, maxLength = 19, example = "10000")
    private Long useCancelReqPt;

    @JsonIgnore
    public UseCancelReqDto getUseCancelReqDto(){
        UseCancelReqDto useCancelReqDto = new UseCancelReqDto();
        useCancelReqDto.setOrgCd(this.orgCd);
        useCancelReqDto.setBrchCd(this.brchCd);
        useCancelReqDto.setContNo(this.contNo);
        useCancelReqDto.setMemberId(this.memberId);
        useCancelReqDto.setOrglTrSno(this.orglTrSno);
        useCancelReqDto.setUseCancelReqPt(this.useCancelReqPt);
        return useCancelReqDto;
    } 
}
