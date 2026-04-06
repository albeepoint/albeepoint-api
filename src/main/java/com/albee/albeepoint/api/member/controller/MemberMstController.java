package com.albee.albeepoint.api.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
 
import com.albee.albeepoint.api.common.dto.ResponseDto;
import com.albee.albeepoint.api.common.dto.ResultListDto;
import com.albee.albeepoint.api.common.dto.TrLogDto;
import com.albee.albeepoint.api.common.exception.AlbeepointException;
import com.albee.albeepoint.api.common.service.TrLogService;
import com.albee.albeepoint.api.member.dto.MemberIdReqDto;
import com.albee.albeepoint.api.member.dto.MemberInfoDto;
import com.albee.albeepoint.api.member.dto.MemberInfoResDto;
import com.albee.albeepoint.api.member.dto.MemberListReqDto;
import com.albee.albeepoint.api.member.dto.MemberMstDto;
import com.albee.albeepoint.api.member.dto.MemberReqDto;
import com.albee.albeepoint.api.member.dto.MemberResDto;
import com.albee.albeepoint.api.member.service.MemberMstService;
import com.albee.albeepoint.api.util.ComUtil;

@Log4j2
@RestController
@EnableWebMvc
@RequiredArgsConstructor
@RequestMapping(value = "/member", produces="application/json;charset=UTF-8")
@Tag(name = "회원 관리", description = "회원 및 회원식별자 관리 API")
public class MemberMstController {
    @Autowired
    private MemberMstService memberMstService;

    @Autowired
    private TrLogService trLog;

    @Operation(summary = "기관 회원 등록", description = "기관 회원 등록")
    @PostMapping("/reg")
    public ResponseEntity<ResponseDto> regMemberMst(
            HttpServletRequest request,
            @Parameter(description = "기관 회원 등록", required = true) @RequestBody MemberReqDto reqDto
    ){
        trLog.regTrLog(request, reqDto);

        try{
            this.memberMstService.regMemberMst(reqDto);
        }catch(AlbeepointException e){
            return ResponseEntity.ok().body(new ResponseDto<>(e));
        }

        return ResponseEntity.ok().body(new ResponseDto<>());
    }

    @Operation(summary = "기관 회원 수정", description = "기관 회원 수정")
    @PostMapping("/mod")
    public ResponseEntity<ResponseDto> modMemberMst(
            HttpServletRequest request,
            @Parameter(description = "기관 회원 수정", required = true) @RequestBody MemberReqDto reqDto
    ){

        trLog.regTrLog(request, reqDto);

        try{
            this.memberMstService.modMemberMst(reqDto);
        }catch(AlbeepointException e){
            return ResponseEntity.ok().body(new ResponseDto<>(e));
        }

        return ResponseEntity.ok().body(new ResponseDto<>());
    }

    @Operation(summary = "기관 회원 조회", description = "기관 회원 조회")
    @PostMapping("")
    public ResponseEntity<ResponseDto<MemberInfoResDto>> getMemberMst(
            HttpServletRequest request,
            @Parameter(description = "기관 회원 조회", required = true) @RequestBody MemberIdReqDto reqDto
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqDto);
        MemberInfoDto result = memberMstService.getMemberInfoWithPid(reqDto.getMemberSearch());

        MemberInfoResDto resDto = (MemberInfoResDto) ComUtil.objectCopy(result, MemberInfoResDto.class);

        trLog.modTrLog(trLogDto, resDto);
        return ResponseEntity.ok().body(new ResponseDto<>(resDto));
    }

    @Operation(summary = "회원 목록 조회", description = "회원 목록 조회")
    @PostMapping("/list")
    public ResponseEntity<ResponseDto<ResultListDto<MemberResDto>>> getMemberMstList(
            HttpServletRequest request,
            @Parameter(description = "회원 목록 조회", required = true) @RequestBody MemberListReqDto reqDto
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqDto);
        ResultListDto<MemberMstDto> result = memberMstService.getMemberMstList(reqDto.getMemberSearch());

        ResultListDto<MemberResDto> resDto = ComUtil.objectResultListCopy(result, MemberResDto.class);
        trLog.modTrLog(trLogDto, resDto);
        return ResponseEntity.ok().body(new ResponseDto<>(resDto));
    }

}
