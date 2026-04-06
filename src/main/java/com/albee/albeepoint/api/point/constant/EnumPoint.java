package com.albee.albeepoint.api.point.constant;
 
import com.albee.albeepoint.api.common.constant.CodeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface EnumPoint {

    @Getter
    @AllArgsConstructor
    enum TrGb implements CodeEnum {
        S001("S001", "적립", "적립")
        ,SC01("SC01", "적립취소", "적립취소")
        ,U001("U001", "사용", "사용")
        ,UC01("UC01", "사용취소", "사용취소")
        ,EX01("S001", "소멸", "소멸")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }

    @Getter
    @AllArgsConstructor
    enum TrMethodGb implements CodeEnum {
        ONLINE("ONLINE", "온라인", "실시간 온라인 트랜젝션")
        ,BATCH("BATCH", "배치", "배치 트랜젝션")
        ,USE_ADD_ISU("USE_ADD_ISU", "사용추가적립", "사용 후 자동 추가 적립")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }
}
