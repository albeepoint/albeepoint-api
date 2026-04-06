package com.albee.albeepoint.api.common.constant;
 

public final class EnumDescConst {
    public static final String descPtType = "포인트유형 - 포인트 운영 유형 [MILEAGE:마일리지 유형, COUPON:쿠폰 유형]";

    public static final String descUseStartDtCalcType = "사용 시작 일자 계산 유형 - 포인트를 사용 가능한 시작일자를 지정하는 방법" +
            "[NONE:미지정(적립 후 즉시 사용가능), DAY:일, WEEK:주, MONTH:월, YEAR:연]";

    public static final String descUsePeriodCalcType = "사용 가능 기간 계산 유형 - 포인트를 사용 가능한 기간을 지정하는 방법" +
            "[NONE:미지정(사용시작일 이후 기간 제한 없이 사용가능), DAY:일, WEEK:주, MONTH:월, YEAR:연]";

    public static final String descContSts = "계약 상태 - 계약 중지 여부 판단" +
            "[NORMAL:정상 상태(계약기간과 관련없이 강제 중이가 아니면 정상 상태), STOP:강제 중]";

    public static final String descIsuPeriodLimitType = "기간별 적립 제한 유형" +
            "[NONE:미지정(기간별 적립 제한 없음), DAY:일, WEEK:주, MONTH:월, YEAR:연]";

    public static final String descUsePeriodLimitType = "기간별 사용 제한 유형 " +
            "[NONE:미지정(기간별 사용 제한 없음), DAY:일, WEEK:주, MONTH:월, YEAR:연]";


    public static final String descOnceIsuLimitType = "1회 적립 제한 유형 " +
            "[NONE:미지정(1회 적립 제한 없음), FIX:정액 적립, RATE:정률 적립(구매금액 대비 적립 비율), MIN:최소 적립 제한, MAX 최대 적립 제한, BOTH:최소최대 적립 제한]";

    public static final String descOnceUseLimitType = "1회 사용 제한 유형 " +
            "[NONE:미지정(1회 사용 제한 없음), FIX:정액 사용, RATE:정률 사용(구매금액 대비 사용 비율), MIN:최소 사용 제한, MAX 최대 사용 제한, BOTH:최소최대 사용 제한]";

    public static final String descTotalIsuLimitType = "최대 적립 제한 유형 " +
            "[NONE:미지정(최대 적립 제한 없음), MAX_PT:최대 적립 포인트 제한, MAX_CNT:최대 적립 건수 제한, BOTH:최대 적립 건수 및 포인트 제한]";



    private static <E extends Enum<E>> String enumList(Class<E> enumType){
        String enumList = "" + AlbeeConst.crlf;
        for(E e : enumType.getEnumConstants()){
            enumList = enumList + ((CodeEnum)e).getCode() + ":" + ((CodeEnum) e).getCodeNm() + "(" + ((CodeEnum) e).getCodeDesc() + ")" + AlbeeConst.crlf;
        }
        enumList = enumList + "";

        return enumList;
    }

    private static <E extends Enum<E>> String enumListJson(Class<E> enumType){
        String enumList = "{" + AlbeeConst.crlf;
        for(E e : enumType.getEnumConstants()){
            enumList = enumList + "\t" + "\"" + ((CodeEnum)e).getCode() + "\" : \"" + ((CodeEnum) e).getCodeNm() + "\"" + AlbeeConst.crlf;
        }
        enumList = enumList + "}";

        return enumList;
    }
}
