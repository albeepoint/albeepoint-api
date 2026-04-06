package com.albee.albeepoint.mapper.member;
 
import org.springframework.stereotype.Component;

import com.albee.albeepoint.api.member.dto.MemberMstDto;
import com.albee.albeepoint.api.member.dto.MemberSearchDto;

import java.util.List;

@Component
public interface MemberMstMapper{
    long selectSeqMemberMstNo();

    MemberMstDto selectMemberMst(MemberSearchDto dom);

    MemberMstDto selectMemberMstByIdOrPid(MemberSearchDto dom);

    Long selectMemberMstListTotalCnt(MemberSearchDto dom);

    List<MemberMstDto> selectMemberMstList(MemberSearchDto dom);
}
