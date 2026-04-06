package com.albee.albeepoint.api.contract.dto;
 
import com.albee.albeepoint.api.common.dto.SearchDto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class ContOrgSearchDto extends SearchDto {
    private Long contNo;    // 계약번호
    private Long contSeq;    // 계약순번

    public ContOrgSearchDto(Long contNo, Long contSeq, Long orgNo) {
        this.contNo = contNo;
        this.contSeq = contSeq;
        //this.contSeq = contSeq != null ? contSeq : 0L;
        this.setOrgNo(orgNo);
    }

    public ContOrgSearchDto(Long contNo, Long contSeq, Long orgNo, Long brchNo) {
        this.contNo = contNo;
        this.contSeq = contSeq;
        //this.contSeq = contSeq != null ? contSeq : 0L;
        this.setOrgNo(orgNo);
        this.setBrchNo(brchNo);
    }

    public ContOrgSearchDto(Long contNo) {
        this.contNo = contNo;
        this.contSeq = 0L;
    }

    public ContOrgSearchDto(Long contNo, Long contSeq) {
        this.contNo = contNo;
        this.contSeq = contSeq;
        //this.contSeq = contSeq != null ? contSeq : 0L;
    }


}
