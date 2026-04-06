package com.albee.albeepoint.api.common.constant;
  

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface EnumCommon {

    @Getter
    @AllArgsConstructor
    enum EncTarget implements CodeEnum {
        ORG("ORG", "기관", "기관")
        ,MEMBER("MEMBER", "회원", "회원")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }

    // 암호화상태코드
    @Getter
    @AllArgsConstructor
    enum EncSts implements CodeEnum {
        NORMAL("NORMAL", "정상", "정상")
        ,STOP("STOP", "정지", "정지")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }
    
}
