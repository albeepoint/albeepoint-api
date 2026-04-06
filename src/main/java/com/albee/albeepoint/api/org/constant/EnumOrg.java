package com.albee.albeepoint.api.org.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface EnumOrg {

    @Getter
    @AllArgsConstructor
    enum LocalGb {
        ALL("ALL", "전체", "전체")
        ,ORG("ORG", "기관", "기관")
        ,BRCH("BRCH", "지점", "지점")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }

    @Getter
    @AllArgsConstructor
    enum OrgSts {
        // 상태코드
        NORMAL("NORMAL", "정상", "정상 기관")
        ,STOP("STOP", "중지", "중지 기관")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }

    @Getter
    @AllArgsConstructor
    enum BrchSts {
        // 상태코드
        NORMAL("NORMAL", "정상", "정상 지점")
        ,STOP("STOP", "중지", "중지 지점")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }

    // 필요없는 컬럼 같음.
    // 그냥 심플하게 CONT_BRCH 에 존재하면 해당 계약으로 적용. 없으면 CONT_ORG 적용
    @Getter
    @AllArgsConstructor
    enum BrchPolicyType {
        ALL("ALL", "전체", "전체 지점 해당")
        ,EXCLUDE_ONLY("EXCLUDE_ONLY", "제외", "지정 지점 제외")   // 기명지점제외 모두 해당
        ,INCLUDE_ONLY("INCLUDE_ONLY", "포함", "지정 지점 포함")   // 기명지점만 해당
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }
}
