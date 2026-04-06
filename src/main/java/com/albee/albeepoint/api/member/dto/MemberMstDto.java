package com.albee.albeepoint.api.member.dto;
 
import com.albee.albeepoint.api.common.constant.AlbeeConst;
import com.albee.albeepoint.api.common.dto.BaseDto;
import com.albee.albeepoint.api.member.constant.EnumMember;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.sql.Timestamp;


@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class MemberMstDto extends BaseDto {
    @JsonIgnore
    private Long rowNum;

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
    
    @JsonFormat(pattern = AlbeeConst.timestampJsonPattern, timezone = AlbeeConst.defaultTimeZone)
    private Timestamp lastContactDt;    // 최종접촉일시
}
