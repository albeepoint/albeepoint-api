package com.albee.albeepoint.api.member.dto;

import com.albee.albeepoint.api.member.constant.EnumMember;
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.mapper.base.t_member_mst.TMemberMst; 
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
@Schema(description = "기관 회원 등록 VO")
public class MemberListReqDto {
    @Schema(description = "기관코드", requiredMode = REQUIRED, maxLength = 20, example = "ORG001")
    private String orgCd;

    @Schema(description = "회원ID", requiredMode = REQUIRED, maxLength = 100, example = "aaa001")
    private String memberId;    // 회원식별자

    @Schema(description = "회원명", requiredMode = NOT_REQUIRED, maxLength = 100, example = "홍길동")
    private String memberNm;    // 회원명

    @Schema(description = "회원상태코드[NORMAL:정상, STOP:중지, LEAVE:탈퇴]", requiredMode = NOT_REQUIRED, defaultValue = "NORMAL", maxLength = 20, example = "NORMAL")
    private EnumMember.MemberSts memberStsCd;    // 회원상태코드(NORMAL:정상, UNNORMAL:미사용, LEAVE:탈퇴)


    @Schema(description = "조회 시작 인덱스", requiredMode = NOT_REQUIRED, example = "51")
    private Integer startIndex;

    @Schema(description = "조회 종료 인덱스", requiredMode = NOT_REQUIRED, example = "100")
    private Integer endIndex;

    public MemberListReqDto(String orgCd, String memberId) {
        this.orgCd = orgCd;
        this.memberId = memberId;
    }

    @JsonIgnore
    public TMemberMst getEntity(){
        TMemberMst entity = new TMemberMst();
        ComUtil.objectCopy(this, entity);
        return entity;
    }

    @JsonIgnore
    public MemberSearchDto getMemberSearch(){
        MemberSearchDto mbrSearch = new MemberSearchDto();
        mbrSearch.setOrgCd(this.orgCd);
        mbrSearch.setMemberId(this.memberId);
        return mbrSearch;
    }

}