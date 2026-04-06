package com.albee.albeepoint.api.point.dto;
 
import lombok.*;

import java.sql.Timestamp;

import com.albee.albeepoint.api.common.dto.BaseDto;
import com.albee.albeepoint.api.contract.constant.EnumCont;
import com.albee.albeepoint.api.contract.dto.ContMstDto;
import com.albee.albeepoint.api.contract.dto.ContRealDto;
import com.albee.albeepoint.api.member.dto.MemberMstDto;
import com.albee.albeepoint.api.point.constant.EnumPoint;
import com.albee.albeepoint.mapper.base.t_brch_mst.TBrchMst;
import com.albee.albeepoint.mapper.base.t_cont_mst.TContMst;
import com.albee.albeepoint.mapper.base.t_member_mst.TMemberMst;
import com.albee.albeepoint.mapper.base.t_member_pt_mst.TMemberPtMst;
import com.albee.albeepoint.mapper.base.t_org_mst.TOrgMst;
import com.albee.albeepoint.mapper.base.t_pt_mst.TPtMst;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BasePtReqDto extends BaseDto {

    private EnumCont.PtType ptTypeCd;

    private Long contNo;

    private String contNm;

    private Long orgNo;

    private Long brchNo;

    private Long memberNo;

    private String memberId;

    private String memberNm;

    private String orgCd;

    private String brchCd;

    private Long memberPtNo;

    private Long ptNo;

    private EnumPoint.TrGb trGbCd;

    private Long balPt;

    private Long contBalPt;

    private Long trPt;

    private Long purchaseAmt;

    private Long orglTrSno;

    private Timestamp baseIsuDt;               // 기준발급일시

    private Timestamp isuDt;               // 발급일시

    private Timestamp useStartDt;          // 사용시작일시

    private Timestamp useEndDt;            // 사용종료일시

    private Timestamp useCancelStartDt;    // 사용취소시작일시

    private Timestamp useCancelEndDt;    // 사용취소종료일시

    private Timestamp ExpDt;    // 소멸일시


    private ContMstDto cont;

    private TOrgMst org;

    private TBrchMst brch;

    private TMemberMst member;

    private TMemberPtMst memberPt;

    private TPtMst pt;

    private ContRealDto contReal;

    public void setCont(ContMstDto dom){
        this.cont = dom;
        this.contNo = dom != null && dom.getContNo() != null ? dom.getContNo() : null;
        this.ptTypeCd = dom != null && dom.getPtTypeCd() != null ? dom.getPtTypeCd() : null;
    }

    public void setOrg(TOrgMst dom){
        this.org = dom;
        this.orgNo = dom != null && dom.getOrgNo() != null ? dom.getOrgNo() : null;
        this.orgCd = dom != null && dom.getOrgCd() != null ? dom.getOrgCd() : null;
    }

    public void setBrch(TBrchMst dom){
        this.brch = dom;
        this.brchNo = dom != null && dom.getBrchNo() != null ? dom.getBrchNo() : null;
        this.brchCd = dom != null && dom.getBrchCd() != null ? dom.getBrchCd() : null;
    }

    public void setMember(MemberMstDto dom){
        this.member = dom;
        this.memberNo = dom != null && dom.getMemberNo() != null ? dom.getMemberNo() : null;
        this.memberId = dom != null && dom.getMemberId() != null ? dom.getMemberId() : null;
    }

    public void setMemberPt(TMemberPtMst dom){
        this.memberPt = dom;
    }
}
