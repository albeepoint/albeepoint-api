package com.albee.albeepoint.api.common.dto;
  
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class EncInfoSearchDto extends SearchDto {
    private String targetGbCd;    // 대상구분코드(ORG:기관, MEMBER:회원)
    private String targetVal;    // 대상값(기관번호, 회원번호)

    public EncInfoSearchDto(String targetGbCd, String targetVal) {
        this.targetGbCd = targetGbCd;
        this.targetVal = targetVal;
    }

    public EncInfoSearchDto(EncInfoDto dto) {
        this.targetGbCd = dto.getTargetGbCd();
        this.targetVal = dto.getTargetVal();
    }
}
