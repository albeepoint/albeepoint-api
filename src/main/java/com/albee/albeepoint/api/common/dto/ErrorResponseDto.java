package com.albee.albeepoint.api.common.dto;
 
import com.albee.albeepoint.api.common.constant.ErrorCode;

public class ErrorResponseDto extends ResponseDto {
//    private int status;
//    private String message;
//    private String code;


    public ErrorResponseDto(ErrorCode errorCode){
//        this.status = errorCode.getStatus();
//        this.message = errorCode.getMessage();
//        this.code = errorCode.getErrorCode();
        super.setRspCode(errorCode.getErrorCode());
        super.setRspMsg(errorCode.getMessage());
    }
}