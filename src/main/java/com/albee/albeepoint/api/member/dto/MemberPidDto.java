package com.albee.albeepoint.api.member.dto;
 
import com.albee.albeepoint.api.common.dto.BaseDto;
import com.albee.albeepoint.api.member.constant.EnumMember;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MemberPidDto extends BaseDto {
    @JsonIgnore
    private Long rowNum;

    @JsonIgnore
    private Long pidNo;    // 개인식별자번호

    @JsonIgnore
    private Long orgNo;

    private String orgCd;

    @JsonIgnore
    private Long memberNo;    // 회원번호

    private String memberId;

    private String pid;    // 개인식별자

    @JsonIgnore
    private String pidEnc;    // 개인식별자암호화

    private EnumMember.PidSts pidStsCd;    // 개인식별자(NORMAL, STOP)

    private Timestamp joinDt;    // 가입일시

    private Timestamp stopDt;    // 중일시
}
