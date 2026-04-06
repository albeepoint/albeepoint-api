package com.albee.albeepoint.api.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;


public interface EnumFilter {

    @Getter
    @AllArgsConstructor
    enum BizRole {
        ALL("ALL", "전체")
        ,ISU("ISU", "적립")
        ,USE("USE", "사용")
        ,MGMT("MGMT", "관리")
        ;

        private String code;
        private String codeNm;
    }

    @Getter
    @AllArgsConstructor
    enum UriTrGb {
        ALL("ALL", "전체")
        ,ISU("ISU", "적립")
        ,USE("USE", "사용")
        ,MGMT("MGMT", "관리")
        ;

        private String code;
        private String codeNm;
    }

    @Getter
    @AllArgsConstructor
    enum AcsPolicy {
        ALL("ALL", "전체")
        ,WHITE("WHITE", "화이트리스트")
        ;

        private String code;
        private String codeNm;
    }
}
