package com.albee.albeepoint.api.member.constant;
 
import com.albee.albeepoint.api.common.constant.CodeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface EnumMember {

    @Getter
    @AllArgsConstructor
    enum MemberSts implements CodeEnum {
        // 상태코드
        NORMAL("NORMAL", "정상", "정상 회원")
        ,STOP("STOP", "중지", "중지 회원")
        ,LEAVE("LEAVE", "탈퇴", "탈퇴 회원")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }

    @Getter
    @AllArgsConstructor
    enum PidSts implements CodeEnum {
        // 상태코드
        NORMAL("NORMAL", "정상", "정상 회원식별자")
        ,STOP("STOP", "중지", "중지 회원식별자")
        ,LEAVE("LEAVE", "탈퇴", "탈퇴 회원식별자")
        ;

        private String code;
        private String codeNm;
        private String codeDesc;
    }
}
