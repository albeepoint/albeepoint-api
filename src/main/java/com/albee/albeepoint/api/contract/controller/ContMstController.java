package com.albee.albeepoint.api.contract.controller;

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
import com.albee.albeepoint.api.common.dto.ResultListDto;
import com.albee.albeepoint.api.common.dto.TrLogDto;
import com.albee.albeepoint.api.common.service.TrLogService;
import com.albee.albeepoint.api.contract.dto.ContModReqDto;
import com.albee.albeepoint.api.contract.dto.ContMstDto;
import com.albee.albeepoint.api.contract.dto.ContRegReqDto;
import com.albee.albeepoint.api.contract.service.ContMstService;
import com.albee.albeepoint.api.util.ComUtil;

@RestController
@EnableWebMvc
@RequiredArgsConstructor
@RequestMapping(value = "/cont", produces="application/json;charset=UTF-8")
@Tag(name = "계약 관리", description = "포인트 서비스를 위한 계약 관리 API")
public class ContMstController {
    @Autowired
    private ContMstService contMstService;
    @Autowired
    private TrLogService trLog;

    public static final Logger log = LogManager.getLogger(ContMstController.class);

    @Operation(summary = "계약 등록", description = "포인트 서비스 계약 등록")
    @PostMapping("/reg")
    public ResponseEntity<ResponseDto> regContMst(
        HttpServletRequest request,
        @Parameter(description = "계약 등록 정보", required = true) @RequestBody ContRegReqDto reqDto
    ){
        trLog.regTrLog(request, reqDto);

        try{
            this.contMstService.regContMst(reqDto.getIVo());
        }catch(Exception e){
            log.error("계약 등록 중 오류 발생", e);
            return ResponseEntity.ok().body(new ResponseDto<>(e.getMessage()));
        }

        return ResponseEntity.ok().body(new ResponseDto<>());
    }

    @Operation(summary = "계약 수정", description = "포인트 서비스 계약 수정")
    @PostMapping("/mod")
    public ResponseEntity<ResponseDto<ContResVo>> modContMst(
            HttpServletRequest request,
            @Parameter(description = "계약 수정 정보", required = true) @RequestBody ContModReqDto reqDto
    ){
        trLog.regTrLog(request, reqDto);

        try{
            this.contMstService.modContMst(reqDto);
        }catch(Exception e){
            log.error("계약 수정 중 오류 발생", e);
            return ResponseEntity.ok().body(new ResponseDto<>(e.getMessage()));
        }

        return ResponseEntity.ok().body(new ResponseDto<>());
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        ContMstDto result = contMstService.modContMst(reqVo.getIVo());

        ContResVo resVo = (ContResVo) ComUtil.objectCopy(result, ContResVo.class);
        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

    @Operation(summary = "계약 조회", description = "포인트 서비스 계약 정보 조회")
    @PostMapping("")
    public ResponseEntity<ResponseDto<ContResVo>> getContMst(
            HttpServletRequest request,
            @Parameter(description = "계약 번호", required = true) @RequestBody ContNoReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        ContMstDto result = contMstService.getContMstWec(reqVo.getContSearch());

        ContResVo resVo = (ContResVo)ComUtil.objectCopy(result, ContResVo.class);
        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

    @Operation(summary = "계약 목록 조회", description = "포인트 서비스 계약 목록 조회")
    @PostMapping("/list")
    public ResponseEntity<ResponseDto<ResultListDto<ContResVo>>> getContMstList(
            HttpServletRequest request,
            @Parameter(description = "계약 목록 조회", required = true) @RequestBody ContListReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        ResultListDto<ContMstDto> resultList = contMstService.getContMstList(reqVo.getContSearch());

        ResultListDto<ContResVo> resVo = ComUtil.objectResultListCopy(resultList, ContResVo.class);
        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

}
