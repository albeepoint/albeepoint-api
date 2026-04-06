package com.albee.albeepoint.api.common.exception;
 
import com.albee.albeepoint.api.common.constant.ErrorCode;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AlbeepointException extends RuntimeException{
    private ErrorCode errorCode;

    public AlbeepointException(ErrorCode errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }

    public AlbeepointException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public String getCode(){
        return this.errorCode.getCode();
    }
}
