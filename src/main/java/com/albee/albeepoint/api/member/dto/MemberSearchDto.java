package com.albee.albeepoint.api.member.dto;
 
import com.albee.albeepoint.api.common.dto.SearchDto;
import com.albee.albeepoint.api.member.constant.EnumMember;
import com.albee.albeepoint.mapper.base.t_member_mst.TMemberMst;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class MemberSearchDto extends SearchDto {

    private EnumMember.MemberSts memberStsCd;

    public MemberSearchDto(Long memberNo) {
        super.setMemberNo(memberNo);
    }

    public MemberSearchDto(String orgCd, String memberId) {
        super.setOrgCd(orgCd);
        super.setMemberId(memberId);
    }

    public MemberSearchDto(String memberId) {
        super.setMemberId(memberId);
    }

    public MemberSearchDto(TMemberMst entity) {
        super.setMemberNo(entity.getMemberNo());
    }


    public String getMemberStsCd(){
        if(this.memberStsCd == null){
            return "NORMAL";
        }

        return this.memberStsCd.toString();
    }
}
