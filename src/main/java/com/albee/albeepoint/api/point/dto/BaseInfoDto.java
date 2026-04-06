package com.albee.albeepoint.api.point.dto;
 
import com.albee.albeepoint.api.util.ComUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data 
public class BaseInfoDto {
    private String memberId;    // 회원ID

    private Long memberNo;

    private Long contNo;    // 계약번호

    private String orgCd;    // 기관코드

    private Long orgNo;

    private String brchCd;    // 지점코드

    private Long brchNo;

    private Long reqPt;

    public BaseInfoDto(Object obj){
        ComUtil.objectCopy(obj, this);
    }

    public void setData(Object obj){
        ComUtil.objectCopy(obj, this);
    }
}
