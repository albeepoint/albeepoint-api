package com.albee.albeepoint.api.point.dto;
 
import com.albee.albeepoint.api.common.dto.BaseDto;

import lombok.*;


@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MemberPtDto extends BaseDto {
    private Long memberPtNo;          // 회원포인트번호

    private Long memberNo;            // 회원번호

    private String memberId;            // 회원ID

    private Long contNo;           // 계약번호

    private String contNm;           // 계약명

    private Long orgNo;            // 기관번호

    private String orgCd;

    private Long ablePt;              // 기간별 사용제한 등 고려한 사용가능 포인트

    private Long balPt;               // 잔여PT

    private Long isuPt;

    private Long isuCancelPt;

    private Long usePt;

    private Long useCancelPt;

    private Long expPt;

    public MemberPtDto(Long memberPtNo){
        this.memberPtNo = memberPtNo;
    }

    public MemberPtDto(String memberId, Long contNo, String orgCd) {
        this.memberId = memberId;
        this.contNo = contNo;
        this.orgCd = orgCd;
    }

    public Long getNetIsuPt(){
        return this.isuPt - this.isuCancelPt;
    }

    public Long getNetUsePt(){
        return this.usePt = this.useCancelPt;
    }
}
