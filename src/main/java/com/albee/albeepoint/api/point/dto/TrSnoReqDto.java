package com.albee.albeepoint.api.point.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;


@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 계약 포인트 거래 상세 조회 VO")
public class TrSnoReqDto {
    @Schema(description = "거래번호", requiredMode = NOT_REQUIRED, maxLength = 10, example = "20001")
    private Long trSno;

    @JsonIgnore
    public TrHistSearchDto getTrHistSearch(){
        TrHistSearchDto trHistSearch = new TrHistSearchDto();
        trHistSearch.setTrSno(this.trSno);
        return trHistSearch;
    }

}
