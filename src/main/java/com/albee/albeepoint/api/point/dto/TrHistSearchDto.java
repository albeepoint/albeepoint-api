package com.albee.albeepoint.api.point.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
 
import java.util.List;

import com.albee.albeepoint.api.common.dto.SearchDto;
import com.albee.albeepoint.api.point.constant.EnumPoint;
import com.albee.albeepoint.api.util.VdUtil;
import com.albee.albeepoint.mapper.base.t_tr_hist.TTrHist;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class TrHistSearchDto extends SearchDto {
    private Long trSno;    // 거래일련번호

    private EnumPoint.TrGb trGbCd;

    private Long contNo;    // 계약번호

    private Long orgNo;    // 기관번호

    private Long brchNo;    // 지점번호

    private Long orglTrSno;

    private Long ptNo;

    private Long memberPtNo;

    private String trMethodGbCd;

    private String searchStartDy;

    private String searchEndDy;

    private String searchTrHistDirection;   // 거래내역조회구분. ASC : 오름차순(작은숫자가 위로), DESC : 내림차순(큰숫자가 위로)


    private List<String> trGbCdList;    // 거래구분코드 목록

    private List<PtReqDto> isuCancelTargetList;

    private List<PtReqDto> useTargetList;

    private List<PtReqDto> useCancelTargetList;

    public TrHistSearchDto(Long memberNo, Long trSno) {
        this.setMemberNo(memberNo);
        this.trSno = trSno;
    }

    public TrHistSearchDto(TTrHist entity) {
        this.trSno = entity.getTrSno();
    }


    public String getTrGbCd(){
        return VdUtil.isNotEmpty(this.trGbCd) ? this.trGbCd.toString() : null;
    }
}
