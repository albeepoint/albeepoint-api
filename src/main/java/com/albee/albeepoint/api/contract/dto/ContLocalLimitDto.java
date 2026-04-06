package com.albee.albeepoint.api.contract.dto;
 
import com.albee.albeepoint.api.org.constant.EnumOrg;

import lombok.*;

@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ContLocalLimitDto {
    private EnumOrg.LocalGb localGbCd;
    private Long orgNo;
    private Long brchNo;
    private String saveCanYn;    // 적립가능여부(Y/N)
    private String useCanYn;    // 사용가능여부(Y/N)
    private Long totalIsuAblePt;
    private Long totalIsuAbleCnt;
}
