package com.albee.albeepoint.api.common.dto;
 
import com.albee.albeepoint.api.common.constant.EnumFilter;
import com.albee.albeepoint.api.util.VdUtil;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class IpFilterSearchDto {
    private String ipAddr;
    private Long contNo;
    private EnumFilter.BizRole bizRoleCd;
    private String useYn;

    public IpFilterSearchDto(String ipAddr) {
        this.ipAddr = ipAddr;
        this.useYn = "Y";
    }

    public IpFilterSearchDto(String ipAddr, Long contNo) {
        this.ipAddr = ipAddr;
        this.contNo = contNo;
        this.useYn = "Y";
    }

    public IpFilterSearchDto(String ipAddr, Long contNo, EnumFilter.BizRole bizRoleCd) {
        this.ipAddr = ipAddr;
        this.contNo = contNo;
        this.bizRoleCd = bizRoleCd;
        this.useYn = "Y";
    }

    public IpFilterSearchDto(IpFilterDto dom) {
        this.ipAddr = dom.getIpAddr();
        this.contNo = dom.getContNo();
        this.bizRoleCd = dom.getBizRoleCd();
        this.useYn = "Y";
    }

    public String getBizRoleCd() {
        if(VdUtil.isEmpty(bizRoleCd)){
            return null;
        }

        return bizRoleCd.toString();
    }
}
