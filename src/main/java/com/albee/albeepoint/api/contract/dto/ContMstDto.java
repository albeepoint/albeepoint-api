package com.albee.albeepoint.api.contract.dto;
 
import lombok.*;

import java.sql.Timestamp;

import com.albee.albeepoint.api.common.dto.BaseDto;
import com.albee.albeepoint.api.contract.constant.EnumCont;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ContMstDto extends BaseDto {
    private Long rowNum;

    private Long contNo;    // 계약번호

    private String contNm;    // 계약명

    private EnumCont.PtType ptTypeCd;

    private Timestamp startDt;    // 시작일시

    private Timestamp endDt;    // 종료일시

    private Timestamp isuStartDt;    // 적립시작일시

    private Timestamp isuEndDt;    // 적립종료일시

    private Timestamp useStartDt;    // 사용시작일시

    private Timestamp useEndDt;    // 사용종료일시
 
    private Timestamp contRegDt;    // 계약등록일시

    private EnumCont.UseStartDtCalcType useStartDtCalcTypeCd;    // 사용시작일자계산유형코드(NONE, DAY, WEEK, MONTH, YEAR)

    private Integer useStartDtCalcVal;    // 사용시작일자계산값

    private EnumCont.UsePeriodCalcType usePeriodCalcTypeCd;    // 사용기간계산유형코드(NONE, DAY, WEEK, MONTH, YEAR)

    private Integer usePeriodCalcVal;    // 사용기간계산값

    private EnumCont.ContSts contStsCd;             // 계약상태코드

    private String mixUseYn;    // 혼합사용여부

    private EnumCont.IsuPeriodLimitType isuPeriodLimitTypeCd;    // 적립기간제한유형코드(NONE, DAY, WEEK, MONTH, YEAR, ALL)

    private Long isuPeriodMaxPt;    // 적립기간제한최대포인트

    private Integer isuPeriodMaxCnt;    // 적립기간제한최대횟수

    private EnumCont.OnceIsuLimitType onceIsuLimitTypeCd;    // 1회 적립 포인트 제한 유형코드(NONE:없음, FIX:고정포인트, RATE:구매금액비율, MIN:최소적립포인트, MAX:최대적립포인트, BOTH:최소/최대적립포인트)

    private Long onceIsuFixPt;    // 회당적립고정포인트

    private Double onceIsuPurchaseRate;    // 회당적립구매금액비율

    private Long onceIsuMinPt;    // 회당적립최소포인트

    private Long onceIsuMaxPt;    // 회당적립최대포인트

    private EnumCont.UsePeriodLimitType usePeriodLimitTypeCd;    // 사용기간제한유형코드(DAY, WEEK, MONTH, YEAR, ALL)

    private Long usePeriodMaxPt;    // 사용기간제한최대포인트

    private Integer usePeriodMaxCnt;    // 사용기간제한최대횟수

    private EnumCont.OnceUseLimitType onceUseLimitTypeCd;    // 1회사용제한유형코드

    private Long onceUseFixPt;    // 회당사용고정포인트

    private Double onceUsePurchaseRate;    // 회당사용구매금액비율

    private Long onceUseMinPt;    // 회당사용최소포인트

    private Long onceUseMaxPt;    // 회당사용최대포인트

    private EnumCont.TotalIsuLimitType totalIsuLimitTypeCd;    // 총적립제한유형코드

    private Long totalIsuAblePt;    // 총적립가능포인트

    private Long totalIsuAbleCnt;    // 총적립가능건수

    private Long minPurchaseAmt;    // 최소구매금액

//    public boolean isUsePeriodUnLimit(){
//        if(VdUtil.isEqual(this.ptTypeCd, EnumCont.PtType.MILEAGE)
//            && VdUtil.isEqual(this.usePeriodCalcTypeCd, EnumCont.UsePeriodCalcType.NONE)){
//            return true;
//        }else{
//            return false;
//        }
//    }
}
