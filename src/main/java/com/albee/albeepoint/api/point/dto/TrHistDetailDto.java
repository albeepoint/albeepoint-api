package com.albee.albeepoint.api.point.dto;
 
import lombok.*;

import java.sql.Timestamp;

import com.albee.albeepoint.api.common.dto.BaseDto;
import com.albee.albeepoint.api.point.constant.EnumPoint;


@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TrHistDetailDto extends BaseDto {
    private Long rowNum;

    private Long trSno;    // 거래일련번호

    private Long trSeq;    // 거래순번

    private Long contNo;    // 계약번호

    private String contNm;

    private Long memberNo;    // 회원번호

    private String memberId;    // 회원ID

    private Long ptNo;    // 포인트번호

    private Long memberPtNo;    // 회원포인트번호

    private Timestamp trDt;             // 거래일시

    private EnumPoint.TrGb trGbCd;    // 거래구분코드(S001:적립, U001:사용, SC01:적립취소, UC01:사용취소, EX01:소멸)

    public String getTrGbNm(){
        return this.trGbCd.getCodeNm();
    }

    private EnumPoint.TrMethodGb trMethodGbCd;    // 거래구분코드(ONLINE:실시간적립, BATCH:배치적립)    public String getTrGbNm(){

    public String getTrMethodGbNm(){
        return this.trMethodGbCd.getCodeNm();
    }

    private Long trPt;    // 거래PT

    private Long befBalPt;    // 거래전잔여PT

    private Long afrBalPt;    // 거래후잔여PT

    private Long contBefBalPt;    // 계약거래전잔여PT

    private Long contAfrBalPt;    // 계약거래후잔여PT

    private Long orglTrSno;    // 원거래일련번호


}
