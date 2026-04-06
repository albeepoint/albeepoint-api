package com.albee.albeepoint.api.common.dto;
 
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.mapper.base.t_tr_log.TTrLog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;


@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TrLogDto {
    private String trDt;    // 거래일시
    private String trId;    // 거래ID(UUID)
    private String trGb;    // 거래구분
    private String memberIdEnc;    // 회원번호
    private Long contNo;    // 계약번호
    private String orgCd;    // 기관
    private String brchCd;    // 지점번호
    private String ipAddr;    // IP주소
    private String reqDt;    // 요청일시
    private String resDt;    // 등록일시
    private String reqContent;    // 요청내용
    private String resContent;    // 응답내용

    public TrLogDto(String trGb, Object obj) {
        this.trGb = trGb;
        try {
            this.reqContent = (new ObjectMapper()).writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        ComUtil.objectCopy(obj, this);
    }

    public TTrLog getEntity(){
        TTrLog entity = new TTrLog();
        ComUtil.objectCopy(this, entity);
        return entity;
    }
}
