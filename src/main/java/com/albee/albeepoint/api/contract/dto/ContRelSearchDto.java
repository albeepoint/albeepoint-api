package com.albee.albeepoint.api.contract.dto;
 
import com.albee.albeepoint.api.common.dto.SearchDto;
import com.albee.albeepoint.api.contract.constant.EnumContRel;
import com.albee.albeepoint.api.util.StrUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class ContRelSearchDto extends SearchDto {
    private Long contRelNo;
    private Long contRelSeq;
    private Long contNo;        // 계약번호
    private Long linkContNo;    // 관련계약번호
    private EnumContRel.ContRel contRelGbCd;
    private String contRole;
    private String contRelUseYn;

    public ContRelSearchDto(Long contNo) {
        this.contNo = contNo;
    }

    public ContRelSearchDto(Long contRelNo, String contRelUseYn) {
        this.contRelNo = contRelNo;
        this.contRelUseYn = contRelUseYn;
    }


    public ContRelSearchDto(Long contRelNo, Long contRelSeq, String contRelUseYn) {
        this.contRelNo = contRelNo;
        this.contRelSeq = contRelSeq;
        this.contRelUseYn = contRelUseYn;
    }


    public ContRelSearchDto(Long contRelNo, Long contRelSeq, EnumContRel.ContRel contRelGbCd, String contRelUseYn) {
        this.contRelNo = contRelNo;
        this.contRelSeq = contRelSeq;
        this.contRelGbCd = contRelGbCd;
        this.contRelUseYn = contRelUseYn;
    }

    public ContRelSearchDto(Long contRelNo, EnumContRel.ContRel contRelGbCd, String contRole, String contRelUseYn) {
        this.contRelNo = contRelNo;
        this.contRelGbCd = contRelGbCd;
        this.contRole = contRole;
        this.contRelUseYn = contRelUseYn;
    }

    public String getContRelGbCd() {
        return StrUtil.enumToString(contRelGbCd);
    }
}
