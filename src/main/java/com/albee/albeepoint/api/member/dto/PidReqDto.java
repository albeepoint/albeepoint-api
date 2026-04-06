package com.albee.albeepoint.api.member.dto;
 
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.mapper.base.t_member_pid.TMemberPid;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;


@Data
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(description = "회원 식별자 조회 VO")
public class PidReqDto {
    @Schema(description = "기관코드", requiredMode = REQUIRED, maxLength = 20, example = "ORG001")
    private String orgCd;

    @Schema(description = "회원 식별자", requiredMode = REQUIRED, maxLength = 100, example = "ccc001")
    private String pid;


    public PidReqDto(String orgCd, String pid) {
        this.orgCd = orgCd;
        this.pid = pid;
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
        memberPidSearch.setPid(this.pid);
        return memberPidSearch;
    }
}