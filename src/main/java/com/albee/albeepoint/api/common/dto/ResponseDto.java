package com.albee.albeepoint.api.common.dto;
 
import com.albee.albeepoint.api.common.constant.AlbeeConst;
import com.albee.albeepoint.api.common.constant.ErrorCode;
import com.albee.albeepoint.api.common.exception.AlbeepointException;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@ToString
public class ResponseDto<D> {
    @JsonFormat(pattern = AlbeeConst.timestampJsonPattern, timezone = AlbeeConst.defaultTimeZone)
    private Timestamp rspDt = Timestamp.valueOf(LocalDateTime.now());
    private String rspCode;
    private String rspMsg;
    private D data;

    public ResponseDto() {
        this.rspCode = AlbeeConst.okCd;
        this.rspMsg = AlbeeConst.okMsg;
    }

    public ResponseDto(ErrorCode errorCode) {
        this.rspCode = errorCode.getCode();
        this.rspMsg = errorCode.getMessage();
    }


    public ResponseDto(D data) {
        this.rspCode = AlbeeConst.okCd;
        this.rspMsg = AlbeeConst.okMsg;
        this.data = data;
    }

    public ResponseDto(String rspCode, String rspMsg, D data) {
        this.rspCode = rspCode;
        this.rspMsg = rspMsg;
        this.data = data;
    }

    public ResponseDto(String rspCode, String rspMsg) {
        this.rspCode = rspCode;
        this.rspMsg = rspMsg;
    }

    public ResponseDto(AlbeepointException e) {
        this.rspCode = e.getCode();
        this.rspMsg = e.getMessage();
    }

    public ResponseDto(AlbeepointException e, D data) {
        this.rspCode = e.getCode();
        this.rspMsg = e.getMessage();
        this.data = data;
    }
}
