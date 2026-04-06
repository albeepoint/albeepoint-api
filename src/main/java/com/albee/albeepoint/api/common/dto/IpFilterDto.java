package com.albee.albeepoint.api.common.dto;

import com.albee.albeepoint.api.common.constant.EnumFilter;
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.mapper.base.t_ip_filter.TIpFilter; 
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class IpFilterDto extends BaseDto {
    private String ipAddr;
    private Long contNo;
    private EnumFilter.BizRole bizRoleCd;
    private String useYn;

    public TIpFilter getEntity(){
        TIpFilter entity = new TIpFilter();
        ComUtil.objectCopy(this, entity);
        return entity;
    }
}
