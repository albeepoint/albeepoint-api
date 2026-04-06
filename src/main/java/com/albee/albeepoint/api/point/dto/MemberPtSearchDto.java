package com.albee.albeepoint.api.point.dto;

import lombok.*;


@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MemberPtSearchDto {
    private Long memberPtNo;

    private Long memberNo;    // 회원번호

    private Long contNo;    // 계약번호

    private Long orgNo;    // 기관번호

    private String isuDy;

    private Long baseBalPt;


    public MemberPtSearchDto(Long memberPtNo){
        this.memberPtNo = memberPtNo;
    }

    public MemberPtSearchDto(Long memberNo, Long contNo){
        this.contNo = contNo;
        this.memberNo = memberNo;
    }

    public MemberPtSearchDto(BasePtReqDto dom) {
        this.contNo = dom.getContNo();
        this.orgNo = dom.getOrgNo();
        this.memberNo = dom.getMemberNo();
    }
}
