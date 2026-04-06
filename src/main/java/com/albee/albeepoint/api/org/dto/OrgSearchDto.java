package com.albee.albeepoint.api.org.dto;

import com.albee.albeepoint.api.common.dto.SearchDto;
import com.albee.albeepoint.mapper.base.t_org_mst.TOrgMst; 
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
    
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class OrgSearchDto extends SearchDto {

    private Long orgNo;    // 기관번호
    private String orgCd;    // 기관코드
    private String brchCd;    // 기관코드
    private Long brchNo;

    public OrgSearchDto(Long orgNo) {
        this.orgNo = orgNo;
    }

    public OrgSearchDto(String orgCd) {
        this.orgCd = orgCd;
    }

    public OrgSearchDto(String orgCd, String brchCd) {
        this.orgCd = orgCd;
        this.brchCd = brchCd;
    }

    public OrgSearchDto(TOrgMst entity) {
        this.orgNo = entity.getOrgNo();
    }

}
