package com.albee.albeepoint.api.contract.controller;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.albee.albeepoint.api.common.dto.ResponseDto;
import com.albee.albeepoint.api.common.service.TrLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
 

@RestController
@EnableWebMvc
@RequiredArgsConstructor
@Tag(name = "계약 기관 관리", description = "포인트 서비스를 위한 계약 기관 관리 API")
@RequestMapping(value = "/cont", produces="application/json;charset=UTF-8")
public class ContBrchController {
    @Autowired
    ContBrchService contBrchService;

    @Autowired
    TrLogService trLog;

    public static final Logger log = LogManager.getLogger(ContBrchController.class);

    @Operation(summary = "계약 지점 등록", description = "계약 관련 지점 등록")
    @PostMapping("/brch/reg")
    public ResponseEntity<ResponseDto<Void>> regContBrch(
            HttpServletRequest request,
            @Parameter(description = "계약 지점 등록 정보", required = true) @RequestBody List<ContBrchRegReqDto> reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        List<ContBrchRegReqIVo> reqIVo = (List<ContBrchRegReqIVo>) ComUtil.objectListCopy(reqVo, ContBrchRegReqIVo.class);
        ResultListDto<ContBrchEntity> resVo = contBrchService.regContBrch(reqIVo);

        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, null));
    }

    @Operation(summary = "계약 지점 조회", description = "계약 관련 지점 조회")
    @PostMapping("/brch")
    public ResponseEntity<ResponseDto<ContBrchResVo>> getContBrch(
            HttpServletRequest request,
            @Parameter(description = "계약 지점 조회", required = true) @RequestBody ContBrchReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        ContBrchEntity result = contBrchService.getContBrch(reqVo.getContOrgSearch());

        ContBrchResVo resVo = (ContBrchResVo)ComUtil.objectCopy(result, ContBrchResVo.class);

        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

    @Operation(summary = "계약 지점 목록 조회", description = "계약 관련 지점 목록 조회")
    @PostMapping("/brch/list")
    public ResponseEntity<ResponseDto<ResultListDto<ContBrchResVo>>> getContBrchList(
            HttpServletRequest request,
            @Parameter(description = "계약 지점 목록 조회", required = true) @RequestBody ContBrchListReqDto reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        ContOrgSearch dom = new ContOrgSearch();
        ComUtil.objectCopy(reqVo, dom);

        ResultListDto<ContBrchEntity> resultList = contBrchService.getContBrchList(dom);

        ResultListDto<ContBrchResVo> resVo = ComUtil.objectResultListCopy(resultList, ContBrchResVo.class);

        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }
}
