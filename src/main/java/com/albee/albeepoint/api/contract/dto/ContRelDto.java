package com.albee.albeepoint.api.contract.dto;

import com.albee.albeepoint.api.common.dto.BaseDto;
import com.albee.albeepoint.api.contract.constant.EnumCont;
import com.albee.albeepoint.api.contract.constant.EnumContRel;
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.mapper.base.t_cont_rel.TContRel;

import lombok.*;


@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ContRelDto extends BaseDto {
    private Long rowNum;
    private Long contRelNo;    // 계약관계번호
    private Long contRelSeq;    // 계약번호일련번호
    private EnumContRel.ContRel contRelGbCd;    // 계약관계구분코드
    private Long contNo;    // 계약번호
    private Long linkContNo;    // 관련계약번호
    private String contRelInfo;    // 계약관계정보
    private String contRelYn;    // 계약관계사용여부

    public TContRel getEntity(){
        TContRel entity = new TContRel();
        ComUtil.objectCopy(this, entity);
        return entity;
    }
}