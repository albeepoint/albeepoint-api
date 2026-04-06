package com.albee.albeepoint.api.contract.dto;
 
import com.albee.albeepoint.api.common.dto.SearchDto;
import com.albee.albeepoint.mapper.base.t_cont_mst.TContMst;
import com.albee.albeepoint.mapper.base.t_sub_cont_mst.TSubContMst; 
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class ContSearchDto extends SearchDto {
    private Long contNo;    // 계약번호
    private Long contSeq;    // 계약순번
    private Long contHistSeq;
    private Long subContHistSeq;
    private String contNm;

    public ContSearchDto(Long contNo) {
        this.contNo = contNo;
    }

    public ContSearchDto(Long contNo, Long contSeq) {
        this.contNo = contNo;
        this.contSeq = contSeq;
    }

    public ContSearchDto(TContMst entity) {
        this.contNo = entity.getContNo();
    }


    public ContSearchDto(TSubContMst entity) {
        this.contNo = entity.getContNo();
        this.contSeq = entity.getContSeq();
    }

}
