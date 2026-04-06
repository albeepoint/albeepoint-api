package com.albee.albeepoint.api.common.dto;

import com.albee.albeepoint.mapper.base.t_enc_info.TEncInfo;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EncInfoDto extends BaseDto {
    private String targetGbCd;    // 대상구분코드(ORG:기관, MEMBER:회원)
    private String targetVal;    // 대상값(기관번호 또는 회원번호)
    private String encKey;    // 암호화키(AES256)
    private String saltVal;    // 솔트키(SHA256)
    private String encStsCd;    // 암호화상태코드(NORMAL, STOP)

    public TEncInfo getEntity(){
        TEncInfo entity = new TEncInfo();
        entity.setTargetGbCd(this.targetGbCd);
        entity.setTargetVal(this.targetVal);
        entity.setEncKey(this.encKey);
        entity.setSaltVal(this.saltVal);
        entity.setEncStsCd(this.encStsCd);
        return entity;
    }
}
