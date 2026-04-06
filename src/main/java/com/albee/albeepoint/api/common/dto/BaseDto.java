package com.albee.albeepoint.api.common.dto;
 
import com.albee.albeepoint.api.common.constant.AlbeeConst;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseDto {
    @JsonIgnore
    private String regUserId = AlbeeConst.apiUserId;            // 등록자ID

    @JsonIgnore
    private Timestamp regDt;             // 등록일시

    @JsonIgnore
    private String lastModUserId = AlbeeConst.apiUserId;        // 최종수정자ID

    @JsonIgnore
    private Timestamp lastModDt;         // 최종수정일시

}
