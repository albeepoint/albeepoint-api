package com.albee.albeepoint.api.member.dto;

import com.albee.albeepoint.api.common.dto.SearchDto; 
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class MemberPidSearchDto extends SearchDto {
    private Long pidNo;    // 개인식별자번호
    private String pid;    //
    private String pidEnc;    //

    public MemberPidSearchDto(Long pidNo) {
        this.pidNo = pidNo;
    }

    public MemberPidSearchDto(MemberPidDto dto) {
        this.pidNo = dto.getPidNo();
    }

}
