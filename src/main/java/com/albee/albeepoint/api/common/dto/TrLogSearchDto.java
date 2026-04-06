package com.albee.albeepoint.api.common.dto;
 
import lombok.*;


@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TrLogSearchDto extends SearchDto {
    private String trDt;    // 거래일시
    private String trId;    // 거래ID(UUID)
    private String trGb;    // 거래구분
    private String memberIdEnc;    // 회원번호
    private Long contNo;    // 계약번호
    private String orgCd;    // 기관번호
    private String brchCd;    // 지점번호
    private String ipAddr;    // IP주소
    private String reqDt;    // 요청일시
    private String resDt;    // 등록일시
    private String reqContent;    // 요청내용
    private String resContent;    // 응답내용

    public TrLogSearchDto(TrLogDto dom) {
        this.trDt = dom.getTrDt();
        this.trId = dom.getTrId();
        this.trGb = dom.getTrGb();
        this.memberIdEnc = dom.getMemberIdEnc();
        this.contNo = dom.getContNo();
        this.orgCd = dom.getOrgCd();
        this.brchCd = dom.getBrchCd();
        this.ipAddr = dom.getIpAddr();

    }

    public TrLogSearchDto(String trDt, String trId, String trGb, String memberIdEnc, Long contNo, String orgCd, String brchCd, String ipAddr) {
        this.trDt = trDt;
        this.trId = trId;
        this.trGb = trGb;
        this.memberIdEnc = memberIdEnc;
        this.contNo = contNo;
        this.orgCd = orgCd;
        this.brchCd = brchCd;
        this.ipAddr = ipAddr;
    }
}
