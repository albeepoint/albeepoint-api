package com.albee.albeepoint.api.point.dto;
 
import com.albee.albeepoint.api.point.constant.EnumPoint;
import com.albee.albeepoint.api.util.VdUtil;

import lombok.*;

/*
    거래전후잔여포인트 계산 저장용 DTO
 */
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CalcBefAfrBalDto {

    // input
    private EnumPoint.TrGb trGbCd;

    private Long memberPtNo;

    private Long contNo;

    private Long orgNo;

    private Long memberNo;

    private Long ptNo;

    private Long trPt;

    // output
    private Long befBalPt;

    private Long afrBalPt;

    public CalcBefAfrBalDto(Long memberPtNo) {
        this.memberPtNo = memberPtNo;
    }

    public CalcBefAfrBalDto(EnumPoint.TrGb trGbCd, Long contNo, Long orgNo, Long memberNo, Long trPt) {
        this.trGbCd = trGbCd;
        this.contNo = contNo;
        this.orgNo = orgNo;
        this.memberNo = memberNo;
        this.trPt = trPt;
    }

    public CalcBefAfrBalDto(EnumPoint.TrGb trGbCd, Long ptNo, Long trPt) {
        this.trGbCd = trGbCd;
        this.ptNo = ptNo;
        this.trPt = trPt;
    }

    public String getTrGbCd(){
        return VdUtil.isNotEmpty(this.trGbCd) ? this.trGbCd.toString() : null;
    }
}
