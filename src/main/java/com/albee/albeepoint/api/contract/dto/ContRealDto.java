package com.albee.albeepoint.api.contract.dto;
 
import com.albee.albeepoint.api.org.constant.EnumOrg;
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.mapper.base.t_cont_mst.TContMst;

import lombok.*;


@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ContRealDto extends TContMst {
    private EnumOrg.LocalGb localGbCd;
    private Long orgNo;
    private Long brchNo;
    private String saveCanYn;    // 적립가능여부(Y/N)
    private String useCanYn;    // 사용가능여부(Y/N)
    private Long totalIsuAblePt;
    private Long totalIsuAbleCnt;

    public TContMst getCont(){
        TContMst contEty = new TContMst();
        ComUtil.objectCopy(this, contEty);
        return  contEty;
    }

    public void setContLocalLimit(ContLocalLimitDto localLimit){
        this.localGbCd = localLimit.getLocalGbCd();
        this.orgNo = localLimit.getOrgNo();
        this.brchNo = localLimit.getBrchNo();
        this.saveCanYn = localLimit.getSaveCanYn();
        this.useCanYn = localLimit.getUseCanYn();
        this.totalIsuAbleCnt = localLimit.getTotalIsuAbleCnt();
        this.totalIsuAblePt = localLimit.getTotalIsuAblePt();
    }
}