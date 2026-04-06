package com.albee.albeepoint.api.member.dto;
 
import com.albee.albeepoint.api.common.constant.AlbeeConst;
import com.albee.albeepoint.api.member.constant.EnumMember;
import com.albee.albeepoint.api.point.dto.MemberPtDto;
import com.albee.albeepoint.mapper.base.t_member_pid.TMemberPid;
import com.albee.albeepoint.mapper.base.t_member_pt_mst.TMemberPtMst;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;


@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberInfoDto {
    @JsonIgnore
    private Long memberNo;    // 회원번호

    @JsonIgnore
    private Long orgNo;    // 기관번호

    private String orgCd;    // 기관코드

    private String memberId;    // 회원식별자

    @JsonIgnore
    private String memberIdEnc;    // 회원식별자암호화

    private String memberNm;    // 회원명

    private EnumMember.MemberSts memberStsCd;    // 회원상태코드(NORMAL:정상, UNNORMAL:미사용, LEAVE:탈퇴)

    @JsonFormat(pattern = AlbeeConst.timestampJsonPattern, timezone = AlbeeConst.defaultTimeZone)
    private Timestamp joinDt;    // 가입일시

    @JsonFormat(pattern = AlbeeConst.timestampJsonPattern, timezone = AlbeeConst.defaultTimeZone)
    private Timestamp leaveDt;    // 탈퇴일시

    private List<MemberPidDto> pidList;

    private List<MemberPtDto> contPtList;
}