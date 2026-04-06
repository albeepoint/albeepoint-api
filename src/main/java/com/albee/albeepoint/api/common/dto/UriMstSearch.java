package com.albeepoint.core.common.filter.models.dto;

import com.albeepoint.core.common.filter.models.EnumFilter;
import com.albeepoint.core.common.util.CommUtil;
import com.albeepoint.core.common.util.VdUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UriMstSearch {
    private String uri;    // URI
    private EnumFilter.UriTrGb trGbCd;    // 거래구분코드
    private EnumFilter.AcsPolicy acsPolicyCd;    // 접근정책코드(ALL:전체접근, WHITE:등록IP)
    private String useYn;    // 사용여부

    public UriMstSearch(UriMstDto dom) {
        CommUtil.objectCopy(dom, this);
    }

    public UriMstSearch(String uri){
        this.uri = uri;
        this.useYn = "Y";
    }

    public String getTrGbCd(){
        if(VdUtil.isEmpty(this.trGbCd)){
            return null;
        }else {
            return this.trGbCd.toString();
        }
    }
}
