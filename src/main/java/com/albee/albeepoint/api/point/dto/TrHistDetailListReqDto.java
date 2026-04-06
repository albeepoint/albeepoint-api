package com.albee.albeepoint.api.point.dto;
 
import com.albee.albeepoint.api.point.constant.EnumPoint;
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
@Schema(description = "회원 계약 포인트 거래내역 조회 VO")
public class TrHistDetailListReqDto {
    @Schema(description = "거래번호", requiredMode = NOT_REQUIRED, maxLength = 10, example = "20001")
    private Long trSno;

    @Schema(description = "회원ID", requiredMode = REQUIRED, maxLength = 100, example = "aaa001")
    private String memberId;

    @Schema(description = "계약번호", requiredMode = REQUIRED, maxLength = 10, example = "20001")
    private Long contNo;

    @Schema(description = "기관코드", requiredMode = REQUIRED, maxLength = 20, example = "ORG001")
    private String orgCd;

    @Schema(description = "지점코드", requiredMode = NOT_REQUIRED, maxLength = 100, example = "BRCH0001")
    private String brchCd;

    @Schema(description = "거래구분코드(미입력시 전체 거래 종류)[S001:적립, U001:사용, SC01:적립취소, UC01:사용취소, EX01:소멸]", requiredMode = NOT_REQUIRED, defaultValue = "null", example = "S001")
    private EnumPoint.TrGb trGbCd;

    @Schema(description = "조회시작일자", requiredMode = NOT_REQUIRED, maxLength = 8, example = "20230101")
    private String searchStartDy;

    @Schema(description = "조회종료일자", requiredMode = NOT_REQUIRED, maxLength = 8, example = "20230131")
    private String searchEndDy;


    @Schema(description = "조회 시작 인덱스", requiredMode = NOT_REQUIRED, example = "51")
    private Integer startIndex;

    @Schema(description = "조회 종료 인덱스", requiredMode = NOT_REQUIRED, example = "100")
    private Integer endIndex;

    @JsonIgnore
    public TrHistSearchDto getTrHistSearch(){
        TrHistSearchDto trHistSearch = new TrHistSearchDto();
        trHistSearch.setTrSno(this.trSno);
        trHistSearch.setContNo(this.contNo);
        trHistSearch.setMemberId(this.memberId);
        trHistSearch.setOrgCd(this.orgCd);
        trHistSearch.setBrchCd(this.brchCd);
        trHistSearch.setTrGbCd(this.trGbCd);
        trHistSearch.setSearchStartDy(this.searchStartDy);
        trHistSearch.setSearchEndDy(this.searchEndDy);
        trHistSearch.setSearchTrHistDirection("DESC");
        return trHistSearch;
    }

}
