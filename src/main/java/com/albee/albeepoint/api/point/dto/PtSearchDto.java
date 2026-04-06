package com.albee.albeepoint.api.point.dto;
 
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

import com.albee.albeepoint.api.common.dto.SearchDto;
import com.albee.albeepoint.api.contract.constant.EnumCont;
import com.albee.albeepoint.api.point.constant.EnumPoint;
import com.albee.albeepoint.mapper.base.t_cont_mst.TContMst;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PtSearchDto extends SearchDto {
    private Long ptNo;    // 포인트번호

    private Long trSno;    // 거래일련번호

    private EnumPoint.TrGb trGbCd;

    private Long contNo;    // 계약번호

    private String isuDy;

    private Long memberPtNo;

    private List<Long> contNoList;

    private Long reqPt;

    private Long startPtNo;         // 사용처리시 첫번째 PT_NO

    private Long endPtNo;           // 사용처리시 마지막 PT_NO

    private Long endUsePt;          // 사용 업데이트시 마지막 건의 사용처리포인트

    // private List<Long> ptNoList;

    private List<BasePtReqDto> ptReqDtoList;

    private Timestamp isuStartDt;

    private Timestamp isuEndDt;

    private Timestamp useStartDt;

    private Timestamp useEndDt;

    private EnumCont.ContSts contStsCd;

    private TContMst cont;

    private List<PtReqDto> ptReqList;

    public PtSearchDto(Long ptNo){
        this.ptNo = ptNo;
    }

    public PtSearchDto(Long memberNo, Long contNo){
        this.contNo = contNo;
        this.setMemberNo(memberNo);
    }


    public PtSearch(Long memberNo, Long contNo, String isuDy){
        this.contNo = contNo;
        this.setMemberNo(memberNo);
        this.isuDy = isuDy;
    }

    public PtSearch(PtMstEntity entity) {
        this.ptNo = entity.getPtNo();
    }


    public PtSearch(BasePtReqDto dom) {
        this.contNo = dom.getContNo();
        this.setOrgNo(dom.getOrgNo());
        this.setMemberNo(dom.getMemberNo());
    }

    public String getContStsCd(){
        return StrUtil.enumToString(this.contStsCd);
    }

    public String getTrGbCd() {
        return StrUtil.enumToString(this.trGbCd);
    }

}
