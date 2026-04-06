package com.albee.albeepoint.api.point.dto;
 
import com.albee.albeepoint.api.common.constant.AlbeeConst;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;


@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AblePtDetailDto {

    private Long contNo;

    private Long orgNo;

    private Long brchNo;

    private Long ablePt;

    @JsonFormat(pattern = AlbeeConst.timestampJsonPattern, timezone = AlbeeConst.defaultTimeZone)
    private Timestamp isuDt;

    @JsonFormat(pattern = AlbeeConst.timestampJsonPattern, timezone = AlbeeConst.defaultTimeZone)
    private Timestamp expDt;

    @JsonFormat(pattern = AlbeeConst.timestampJsonPattern, timezone = AlbeeConst.defaultTimeZone)
    private Timestamp useEndDt;

    public Timestamp getLastAbleDt(){
        return this.expDt.before(this.useEndDt) ? this.useEndDt : this.expDt;
    }
}
