package com.albee.albeepoint.api.member.dto;

import com.albee.albeepoint.api.common.constant.AlbeeConst;
import com.albee.albeepoint.api.member.constant.EnumMember;
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.mapper.base.t_member_pt_mst.TMemberPtMst; 
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;


@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "기관 회원 등록 응답 DTO")
public class MemberInfoResDto {
    @Schema(description = "기관코드", requiredMode = REQUIRED, maxLength = 20, example = "ORG001")
    private String orgCd;

    @Schema(description = "회원ID", requiredMode = REQUIRED, maxLength = 100, example = "aaa001")
    private String memberId;    // 회원식별자

    @Schema(description = "회원명", requiredMode = NOT_REQUIRED, maxLength = 100, example = "홍길동")
    private String memberNm;    // 회원명

    @Schema(description = "회원상태코드[NORMAL:정상, STOP:중지, LEAVE:탈퇴]", requiredMode = NOT_REQUIRED, defaultValue = "NORMAL", maxLength = 20, example = "NORMAL")
    private EnumMember.MemberSts memberStsCd;    // 회원상태코드(NORMAL:정상, UNNORMAL:미사용, LEAVE:탈퇴)

    @Schema(description = "가입일시", requiredMode = REQUIRED, example = "2023-06-02 00:00:00")
    @JsonFormat(pattern = AlbeeConst.timestampJsonPattern, timezone = AlbeeConst.defaultTimeZone)
    private Timestamp joinDt;    // 가입일시

    @Schema(description = "탈퇴일시", requiredMode = NOT_REQUIRED, example = "2023-06-02 00:00:00")
    @JsonFormat(pattern = AlbeeConst.timestampJsonPattern, timezone = AlbeeConst.defaultTimeZone)
    private Timestamp leaveDt;    // 탈퇴일시


    @Schema(description = "회원 식별자 목록", requiredMode = NOT_REQUIRED)
    private List<MemberPidResDto> pidList;


    @Schema(description = "회원 포인트 목록", requiredMode = NOT_REQUIRED)
    private List<TMemberPtMst> contPtList;


    @JsonIgnore
    public MemberInfoResDto getIVo(){
        return (MemberInfoResDto) ComUtil.votoivoCopy(this);
    }
}