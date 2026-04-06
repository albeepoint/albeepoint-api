package com.albeepoint.api.biz.cont.controller;

import com.albeepoint.api.biz.cont.models.vo.request.ContRelListRegReqVo;
import com.albeepoint.api.biz.cont.models.vo.request.ContRelListReqVo;
import com.albeepoint.api.biz.cont.models.vo.request.ContRelRegReqVo;
import com.albeepoint.api.biz.cont.models.vo.request.ContRelReqVo;
import com.albeepoint.api.biz.cont.models.vo.response.ContRelResVo;
import com.albeepoint.core.biz.cont.models.entity.ContRelEntity;
import com.albeepoint.core.biz.cont.service.ContRelService;
import com.albeepoint.core.common.log.models.dto.TrLogDto;
import com.albeepoint.core.common.log.service.TrLogService;
import com.albeepoint.core.common.models.dto.ResponseDto;
import com.albeepoint.core.common.models.dto.ResultListDto;
import com.albeepoint.core.common.util.CommUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import javax.servlet.http.HttpServletRequest;

import static com.albeepoint.core.common.models.AlbeeConst.okCd;
import static com.albeepoint.core.common.models.AlbeeConst.okMsg;


@RestController
@EnableWebMvc
@RequiredArgsConstructor
@RequestMapping(value = "/cont/rel", produces="application/json;charset=UTF-8")
@Tag(name = "계약 관계 관리", description = "포인트 서비스를 위한 계약간의 관계 관리 API")
public class ContRelController {
    @Autowired
    ContRelService contRelService;
    @Autowired
    TrLogService trLog;

    public static final Logger log = LogManager.getLogger(ContRelController.class);

    @Operation(summary = "계약 관계 등록", description = "계약 관계 등록")
    @PostMapping("/reg")
    public ResponseEntity<ResponseDto<ResultListDto<ContRelResVo>>> regContRel(
            HttpServletRequest request,
            @Parameter(description = "계약 관계 등록", required = true) @RequestBody ContRelListRegReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        ResultListDto<ContRelEntity> resultList = contRelService.regContRel(reqVo.getIVo());

        ResultListDto<ContRelResVo> resVo = ComUtil.objectResultListCopy(resultList, ContRelResVo.class);
        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

    @Operation(summary = "계약 관계 수정", description = "계약 관련 기관 수정")
    @PostMapping("/mod")
    public ResponseEntity<ResponseDto<ContRelResVo>> modContRel(
            HttpServletRequest request,
            @Parameter(description = "계약 관계 수정", required = true) @RequestBody ContRelRegReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        ContRelEntity result = contRelService.modContRel(reqVo.getIVo());

        ContRelResVo resVo = (ContRelResVo)ComUtil.objectCopy(result, ContRelResVo.class);
        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

    @Operation(summary = "계약 관계 조회", description = "계약 관련 기관 조회")
    @PostMapping("")
    public ResponseEntity<ResponseDto<ContRelResVo>> getContRel(
            HttpServletRequest request,
            @Parameter(description = "계약 관계 조회", required = true) @RequestBody ContRelReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        ContRelEntity result = contRelService.getContRel(reqVo.getContRelSearch());

        ContRelResVo resVo = (ContRelResVo)ComUtil.objectCopy(result, ContRelResVo.class);
        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

    @Operation(summary = "계약 관계 목록 조회", description = "계약 관련 기관 목록 조회")
    @PostMapping("/list")
    public ResponseEntity<ResponseDto<ResultListDto<ContRelResVo>>> getContRelList(
            HttpServletRequest request,
            @Parameter(description = "계약 관계 목록 조회", required = true) @RequestBody ContRelListReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        ResultListDto<ContRelEntity> resultList = contRelService.getContRelList(reqVo.getContRelSearch());

        ResultListDto<ContRelResVo> resVo = ComUtil.objectResultListCopy(resultList, ContRelResVo.class);
        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

}
