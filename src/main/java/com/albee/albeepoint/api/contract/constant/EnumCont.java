package com.albee.albeepoint.api.contract.constant;
 
import com.albee.albeepoint.api.common.constant.CodeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface EnumCont {

    @Getter
    @AllArgsConstructor
    enum PtType implements CodeEnum {
        MILEAGE("MILEAGE", "마일리지", "일반적인 포인트 시스템. 수시로 적립/사용 가능")
        ,COUPON("COUPON", "쿠폰", "정해진 포인트를 이벤트성으로 발급")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }

    /*
    @Getter
    @AllArgsConstructor
    enum UseStartDtType implements CodeEnum {
        // 사용 시작일자 지정 유형 코드
        NONE("NONE", "미지정", "적립 즉시 사용 가능")
        ,PERIOD_ADD("PERIOD_ADD", "기간추가", "적립일로부터 특정기간 경과 후 사용 시작")
       // ,FIX_DAY("FIX_DAY", "지정일자", "적립일과 무관하게 미리 지정된 특정일자 부터 사용 시작")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }
     */

    @Getter
    @AllArgsConstructor
    enum UseStartDtCalcType implements CodeEnum {
        // 사용시작일자계산유형코드(TIME(초), DAY, WEEK, MONTH, YEAR)
        NONE("NONE", "미지정", "적립 후 바로 사용 가능")
        ,DAY("DAY", "일", "적립일로부터 지정한 일수 경과 후 사용 가능")
        ,WEEK("WEEK", "주", "적립일로부터 지정한 주(WEEK) 수가 경과한 다음의 월요일부터 사용 가능")
        ,MONTH("MONTH", "월", "적립일로부터 지정한 월(MONTH) 수가 경과한 다음의 1일부터 사용 가능")
        ,YEAR("YEAR", "년", "적립일로부터 지정한 연(YEAR) 수가 경과한 다음의 1월 1일부터 사용 가능")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }

    /*
    @Getter
    @AllArgsConstructor
    enum UsePeriodType {
        // 사용기간유형코드
        NONE("NONE", "미지정", "사용 가능 기간 제한 없음")
        ,PERIOD_ADD("PERIOD_ADD", "기간추가", "사용 시작일로부터 특정기간 경과 후 사용 시작")
        // ,FIX_DAY("FIX_DAY", "지정일자", "사용 시작일과 무관하게 미리 지정된 특정일자 부터 사용 시작")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }

     */

    @Getter
    @AllArgsConstructor
    enum UsePeriodCalcType implements CodeEnum {
        // 사용가능기간계산유형코드(TIME(초), DAY, WEEK, MONTH, YEAR)
        NONE("NONE", "미지정", "사용 시간 제한 없음")
        ,DAY("DAY", "일", "사용 시작일로부터 지정한 일수 경과 후 사용 가능")
        ,WEEK("WEEK", "주", "사용 시작일로부터 지정한 주(WEEK) 수가 경과한 다음의 월요일부터 사용 가능")
        ,MONTH("MONTH", "월", "사용 시작일로부터 지정한 월(MONTH) 수가 경과한 다음의 1일부터 사용 가능")
        ,YEAR("YEAR", "년", "사용 시작일로부터 지정한 연(YEAR) 수가 경과한 다음의 1월 1일부터 사용 가능")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }

/*
    @Getter
    @AllArgsConstructor
    enum UseCancelPeriodType {
        // 사용취소기간유형코드
        NONE("NONE", "미지정")
        ,ISU_ADD("ISU_ADD", "발급일+일수추가")
        ,PERIOD_ADD("PERIOD_ADD", "발급일+기간추가")
        ,FIX_DAY("FIX_DAY", "지정일자")
        ;

        private String code;
        private String codeNm;
    }

 */

    /*
    @Getter
    @AllArgsConstructor
    enum UseCancelPeriodCalcType {
        // 사용취소기간계산유형코드(TIME(초), DAY, WEEK, MONTH, YEAR)
        NONE("NONE", "미지정")
        ,DAY("DAY", "일")
        ,WEEK("WEEK", "주")
        ,MONTH("MONTH", "월")
        ,YEAR("YEAR", "년")
        ;

        private String code;
        private String codeNm;
    }

     */


    @Getter
    @AllArgsConstructor
    enum ContSts implements CodeEnum {
        // 계약상태코드
        NORMAL("NORMAL", "정상", "계약 정상 상태")
        ,STOP("STOP", "중지", "계약 중지 상태")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }

    @Getter
    @AllArgsConstructor
    enum IsuPeriodLimitType implements CodeEnum {
        // 사용기간제한유형코드(DAY, WEEK, MONTH, YEAR)
        NONE("NONE", "미지정", "기간별 적립 제한 없음")
        ,DAY("DAY", "일", "일일 적립 제한")
        ,WEEK("WEEK", "주", "주별 적립 제한")
        ,MONTH("MONTH", "월", "월별 적립 제한")
        ,YEAR("YEAR", "년", "연도별 적립 제한")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }

    @Getter
    @AllArgsConstructor
    enum UsePeriodLimitType implements CodeEnum {
        // 사용기간제한유형코드(TIME(초), DAY, WEEK, MONTH, YEAR)
        NONE("NONE", "미지정", "기간별 사용 제한 없음")
        ,DAY("DAY", "일", "일일 사용 제한")
        ,WEEK("WEEK", "주", "주별 사용 제한")
        ,MONTH("MONTH", "월", "월별 사용 제한")
        ,YEAR("YEAR", "년", "연도별 사용 제한")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }

    @Getter
    @AllArgsConstructor
    enum OnceIsuLimitType implements CodeEnum {
        // 1회 발행 제한 유형코드()
        NONE("NONE", "미지정", "회당 적립 제한 없음")
        ,FIX("FIX", "정액", "회당 적립가능 포인트를 지정한 포인트 이하로 제한")
        ,RATE("RATE", "정률", "회당 적립가능 포인트를 구매금액 대비 비율 이내로  제한")
        ,MIN("MIN", "최소적립", "회당 최소 포인트 이상만 적립 가능")
        ,MAX("MAX", "최대적립", "회당 최대 포인트 이하만 적립 가능")
        ,BOTH("BOTH", "최소 및 최대적립", "회당 최소/최대 포인트 이내만 적립 가능")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }

    @Getter
    @AllArgsConstructor
    enum OnceUseLimitType implements CodeEnum {
        // 1회 사용 제한 유형코드()
        NONE("NONE", "미지정", "회당 사용 제한 없음")
        ,FIX("FIX", "정액", "회당 사용가능 포인트를 지정한 포인트 이하로 제한")
        ,RATE("RATE", "정률", "회당 사용가능 포인트를 구매금액 대비 비율 이내로  제한")
        ,MIN("MIN", "최소사용", "회당 최소 포인트 이상만 사용 가능")
        ,MAX("MAX", "최대사용", "회당 최대 포인트 이하만 사용 가능")
        ,BOTH("BOTH", "최소 및 최대사용", "회당 최소/최대 포인트 이내만 사용 가능")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }

    @Getter
    @AllArgsConstructor
    enum TotalIsuLimitType implements CodeEnum {
        NONE("NONE", "미지정", "총 적립 제한 없음")
        ,MAX_PT("MAX_PT", "최대적립포인트", "총 적립 포인트 제한")
        ,MAX_CNT("MAX_CNT", "최대적립건수", "총 적립 건수 제한")
        ,BOTH("BOTH", "최대적립건수 및 포인트", "총 적립 건수 및 포인트 제한")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }

//    @Getter
//    @AllArgsConstructor
//    enum ContRelGb implements CodeEnum {
//        MIX_USE("MIX_USE", "합산사용", "하나 이상의 계약과 합산 사용 가능")
//        ,ISU_ADD_SEQ("ISU_ADD_SEQ", "순차적추가적립", "등록 순서 방향으로 일방향 추가 적립")
//        ,ISU_ADD_BOTH("NORMAL_ISU_ADD_BOTH", "양방향추가적립", "추가 적립 등록한 모든 계약간 추가 적립 가능")
//        ;
//
//        private String code;
//        private String codeNm;
//        private String codeDesc;
//    }

    @Getter
    @AllArgsConstructor
    enum ContOrgSts implements CodeEnum {
        NORMAL("NORMAL", "정상", "계약 관련 기관 정상 상태")
        ,STOP("STOP", "중지", "계약 관련 기관 중지 상태")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }

    @Getter
    @AllArgsConstructor
    enum ContBrchSts implements CodeEnum {
        NORMAL("NORMAL", "정상", "계약 관련 지점 정상 상태")
        ,STOP("STOP", "중지", "계약 관련 지점 중지 상태")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }
}
