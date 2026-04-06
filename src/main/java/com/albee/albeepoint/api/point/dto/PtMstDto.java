package com.albee.albeepoint.api.point.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class PtMstDto implements Serializable {
    private Long ptNo;

    private Long memberPtNo;

    private Long memberNo;

    private Long contNo;

    private Long orgNo;

    private String isuDy;

    private LocalDateTime isuDt;

    private Long balPt;

    private Long isuPt;

    private Long isuCancelPt;

    private Long usePt;

    private Long useCancelPt;

    private Long expPt;

    private LocalDateTime useStartDt;

    private LocalDateTime useEndDt;

    private LocalDateTime expDt;

    private LocalDateTime expProcDt;

    private String regUserId;

    private LocalDateTime regDt;

    private String lastModUserId;

    private LocalDateTime lastModDt;

    private static final long serialVersionUID = 1L;

    public Long getPtNo() {
        return ptNo;
    }

    public void setPtNo(Long ptNo) {
        this.ptNo = ptNo;
    }

    public Long getMemberPtNo() {
        return memberPtNo;
    }

    public void setMemberPtNo(Long memberPtNo) {
        this.memberPtNo = memberPtNo;
    }

    public Long getMemberNo() {
        return memberNo;
    }

    public void setMemberNo(Long memberNo) {
        this.memberNo = memberNo;
    }

    public Long getContNo() {
        return contNo;
    }

    public void setContNo(Long contNo) {
        this.contNo = contNo;
    }

    public Long getOrgNo() {
        return orgNo;
    }

    public void setOrgNo(Long orgNo) {
        this.orgNo = orgNo;
    }

    public String getIsuDy() {
        return isuDy;
    }

    public void setIsuDy(String isuDy) {
        this.isuDy = isuDy == null ? null : isuDy.trim();
    }

    public LocalDateTime getIsuDt() {
        return isuDt;
    }

    public void setIsuDt(LocalDateTime isuDt) {
        this.isuDt = isuDt;
    }

    public Long getBalPt() {
        return balPt;
    }

    public void setBalPt(Long balPt) {
        this.balPt = balPt;
    }

    public Long getIsuPt() {
        return isuPt;
    }

    public void setIsuPt(Long isuPt) {
        this.isuPt = isuPt;
    }

    public Long getIsuCancelPt() {
        return isuCancelPt;
    }

    public void setIsuCancelPt(Long isuCancelPt) {
        this.isuCancelPt = isuCancelPt;
    }

    public Long getUsePt() {
        return usePt;
    }

    public void setUsePt(Long usePt) {
        this.usePt = usePt;
    }

    public Long getUseCancelPt() {
        return useCancelPt;
    }

    public void setUseCancelPt(Long useCancelPt) {
        this.useCancelPt = useCancelPt;
    }

    public Long getExpPt() {
        return expPt;
    }

    public void setExpPt(Long expPt) {
        this.expPt = expPt;
    }

    public LocalDateTime getUseStartDt() {
        return useStartDt;
    }

    public void setUseStartDt(LocalDateTime useStartDt) {
        this.useStartDt = useStartDt;
    }

    public LocalDateTime getUseEndDt() {
        return useEndDt;
    }

    public void setUseEndDt(LocalDateTime useEndDt) {
        this.useEndDt = useEndDt;
    }

    public LocalDateTime getExpDt() {
        return expDt;
    }

    public void setExpDt(LocalDateTime expDt) {
        this.expDt = expDt;
    }

    public LocalDateTime getExpProcDt() {
        return expProcDt;
    }

    public void setExpProcDt(LocalDateTime expProcDt) {
        this.expProcDt = expProcDt;
    }

    public String getRegUserId() {
        return regUserId;
    }

    public void setRegUserId(String regUserId) {
        this.regUserId = regUserId == null ? null : regUserId.trim();
    }

    public LocalDateTime getRegDt() {
        return regDt;
    }

    public void setRegDt(LocalDateTime regDt) {
        this.regDt = regDt;
    }

    public String getLastModUserId() {
        return lastModUserId;
    }

    public void setLastModUserId(String lastModUserId) {
        this.lastModUserId = lastModUserId == null ? null : lastModUserId.trim();
    }

    public LocalDateTime getLastModDt() {
        return lastModDt;
    }

    public void setLastModDt(LocalDateTime lastModDt) {
        this.lastModDt = lastModDt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", ptNo=").append(ptNo);
        sb.append(", memberPtNo=").append(memberPtNo);
        sb.append(", memberNo=").append(memberNo);
        sb.append(", contNo=").append(contNo);
        sb.append(", orgNo=").append(orgNo);
        sb.append(", isuDy=").append(isuDy);
        sb.append(", isuDt=").append(isuDt);
        sb.append(", balPt=").append(balPt);
        sb.append(", isuPt=").append(isuPt);
        sb.append(", isuCancelPt=").append(isuCancelPt);
        sb.append(", usePt=").append(usePt);
        sb.append(", useCancelPt=").append(useCancelPt);
        sb.append(", expPt=").append(expPt);
        sb.append(", useStartDt=").append(useStartDt);
        sb.append(", useEndDt=").append(useEndDt);
        sb.append(", expDt=").append(expDt);
        sb.append(", expProcDt=").append(expProcDt);
        sb.append(", regUserId=").append(regUserId);
        sb.append(", regDt=").append(regDt);
        sb.append(", lastModUserId=").append(lastModUserId);
        sb.append(", lastModDt=").append(lastModDt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}