package com.albee.albeepoint.api.contract.dto;
 
import com.albee.albeepoint.api.common.dto.BaseDto;
import com.albee.albeepoint.api.contract.constant.EnumCont;
import com.albee.albeepoint.api.org.constant.EnumOrg;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data 
public class ContOrgDto extends BaseDto {
    private Long rowNum;
    private Long contNo;    // 계약번호
    private Long contSeq;    // 계약순번
    private Long orgNo;    // 기관번호
    private EnumOrg.BrchPolicyType brchPolicyTypeCd;    //  지점정책유형코드(ALL:전체, EXCLUDE_ONLY:기명지점제외 모두 해당, INCLUDE_ONLY:기명지점만 해당)

    private String orgCd;
    private String saveCanYn;    // 적립가능여부(Y/N)
    private String useCanYn;    // 사용가능여부(Y/N)
    private EnumCont.ContOrgSts contOrgStsCd;    // 계약기관상태코드
    private Long totalIsuAblePt;
    private Long totalIsuAbleCnt;
}
