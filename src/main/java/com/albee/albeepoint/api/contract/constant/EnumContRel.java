package com.albee.albeepoint.api.contract.constant;
 
import com.albee.albeepoint.api.common.constant.CodeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface EnumContRel {

    @Getter
    @AllArgsConstructor
    enum ContRel implements CodeEnum {
        USE_AFT_ADD_ISU("USE_AFT_ADD_ISU", "사용 후 추가적립", "사용한 후 추가 적립(PARENT/CHILD+CONT_REL_INFO(추가적립순서))")
        ,EXCLUSIVE_USE("EXCLUSIVE_USE", "배타적 사용", "다수 계약의 포인트 보유시 하나의 포인트만 사용")
        ,EXCLUSIVE_ISU("EXCLUSIVE_ISU", "배타적 적립", "다수 계약 중 하나라도 적립시 타 계약 적립 불가")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }

}
