package com.albee.albeepoint.mapper.member;

import com.albee.albeepoint.api.member.dto.MemberPidDto;
import com.albee.albeepoint.api.member.dto.MemberPidSearchDto; 
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface MemberPidMapper{
    Long selectSeqMemberPidNo(); 

    MemberPidDto selectMemberPid(MemberPidSearchDto dom);

    Long selectMemberPidListTotalCnt(MemberPidSearchDto dom);

    List<MemberPidDto> selectMemberPidList(MemberPidSearchDto dom);
}
