package com.albee.albeepoint.api.common.constant;

public interface CodeEnum {
    String getCode();

    String getCodeNm();

    String getCodeDesc();
    
    // 문자열과 code 값을 비교하는 기능 추가
    default boolean equalsCode(String targetCode) {
        if (targetCode == null) return false;
        return getCode().equals(targetCode);
    }
}
