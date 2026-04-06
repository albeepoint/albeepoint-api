package com.albee.albeepoint.api.member.dto;
 
import com.albee.albeepoint.api.member.constant.EnumMember;
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.mapper.base.t_member_pid.TMemberPid;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;


@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 식별자 등록/수정 VO")
public class MemberPidReqDto {
    @Schema(description = "기관코드", requiredMode = REQUIRED, maxLength = 20, example = "ORG001")
    private String orgCd;

    @Schema(description = "회원ID", requiredMode = REQUIRED, maxLength = 100, example = "aaa001")
    private String memberId;    // 회원식별자

    @Schema(description = "회원 식별자", requiredMode = REQUIRED, maxLength = 100, example = "ccc001")
    private String pid;

    @Schema(description = "회원식별자 상태[NORMAL:정상, STOP:중지]", requiredMode = NOT_REQUIRED, defaultValue = "NORMAL", maxLength = 20, example = "NORMAL")
    private EnumMember.PidSts pidStsCd;

    public MemberPidReqDto(String orgCd, String memberId) {
        this.orgCd = orgCd;
        this.memberId = memberId;
    }

    @JsonIgnore
    public TMemberPid getEntity(){
        TMemberPid entity = new TMemberPid();
        ComUtil.objectCopy(this, entity);
        return entity;
    }

    @JsonIgnore
    public MemberPidSearchDto getMemberPidSearch(){
        MemberPidSearchDto memberPidSearch = new MemberPidSearchDto();
        memberPidSearch.setOrgCd(this.orgCd);
        memberPidSearch.setMemberId(this.memberId);
        memberPidSearch.setPid(this.pid);
        return memberPidSearch;
    }

    @JsonIgnore
    public MemberPidReqDto getIVo(){
        return (MemberPidReqDto) ComUtil.votoivoCopy(this);
    }
}