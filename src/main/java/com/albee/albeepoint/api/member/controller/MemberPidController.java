package com.albee.albeepoint.api.member.controller;
 
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.albee.albeepoint.api.common.dto.ResponseDto;
import com.albee.albeepoint.api.common.exception.AlbeepointException;
import com.albee.albeepoint.api.common.service.TrLogService;
import com.albee.albeepoint.api.member.dto.MemberPidReqDto;
import com.albee.albeepoint.api.member.service.MemberPidService;

@RestController
@EnableWebMvc
@RequiredArgsConstructor
@RequestMapping(value = "/member/pid", produces="application/json;charset=UTF-8")
@Tag(name = "회원 관리", description = "회원 및 회원식별자 관리 API")
public class MemberPidController {
    @Autowired
    private MemberPidService memberPidService;
    
    @Autowired
    private TrLogService trLog; 

    @Operation(summary = "회원 식별자 등록", description = "회원 식별자 등록")
    @PostMapping("/reg")
    public ResponseEntity<ResponseDto> regMemberPid(
            HttpServletRequest request,
            @Parameter(description = "회원 식별자 등록", required = true) @RequestBody MemberPidReqDto reqDto
    ){
        trLog.regTrLog(request, reqDto);
        try{
            this.memberPidService.regMemberPid(reqDto); 
        }catch(AlbeepointException e){
            return ResponseEntity.ok().body(new ResponseDto<>(e));
        }
        return ResponseEntity.ok().body(new ResponseDto<>());
    }

    @Operation(summary = "회원 식별자 수정", description = "회원 식별자 수정")
    @PostMapping("/mod")
    public ResponseEntity<ResponseDto<MemberPidResVo>> modMemberPid(
            HttpServletRequest request,
            @Parameter(description = "회원 식별자 수정", required = true) @RequestBody MemberPidReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        MemberPidEntity result = memberPidService.modMemberPid(reqVo.getEntity());

        MemberPidResVo resVo = (MemberPidResVo) CommUtil.objectCopy(result, MemberPidResVo.class);
        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

    @Operation(summary = "회원 식별자 조회", description = "회원 식별자 조회")
    @PostMapping("")
    public ResponseEntity<ResponseDto<MemberPidResVo>> getMemberPid(
            HttpServletRequest request,
            @Parameter(description = "회원 식별자 조회", required = true) @RequestBody MemberPidReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        MemberPidEntity result = memberPidService.getMemberPid(reqVo.getMemberPidSearch());

        MemberPidResVo resVo = (MemberPidResVo) CommUtil.objectCopy(result, MemberPidResVo.class);
        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

    @Operation(summary = "회원 식별자 목록 조회", description = "회원 식별자 목록 조회")
    @PostMapping("/list")
    public ResponseEntity<ResponseDto<ResultListDto<MemberPidResVo>>> getMemberPidList(
            HttpServletRequest request,
            @Parameter(description = "회원 식별자 목록 조회", required = true) @RequestBody MemberPidListReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        ResultListDto<MemberPidEntity> result = memberPidService.getMemberPidList(reqVo.getMemberPidSearch());

        ResultListDto<MemberPidResVo> resVo = CommUtil.objectResultListCopy(result, MemberPidResVo.class);
        trLog.modTrLog(trLogDto, result);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

}
