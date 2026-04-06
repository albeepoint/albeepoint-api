package com.albee.albeepoint.api.member.dto;
 
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.mapper.base.t_member_mst.TMemberMst;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;


@Data
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(description = "회원 식별자 등록 요청 DTO")
public class MemberIdReqDto {
    @Schema(description = "기관코드", requiredMode = REQUIRED, maxLength = 20, example = "ORG001")
    private String orgCd;

    @Schema(description = "회원ID", requiredMode = REQUIRED, maxLength = 100, example = "aaa001")
    private String memberId;

    public MemberIdReqDto(String orgCd, String memberId) {
        this.orgCd = orgCd;
        this.memberId = memberId;
    }

    @JsonIgnore
    public TMemberMst getEntity() {
        TMemberMst entity = new TMemberMst();
        ComUtil.objectCopy(this, entity);
        return entity;
    }

    @JsonIgnore
    public MemberSearchDto getMemberSearch() {
        MemberSearchDto mbrSearch = new MemberSearchDto();
        mbrSearch.setOrgCd(this.orgCd);
        mbrSearch.setMemberId(this.memberId);
        return mbrSearch;
    }
}