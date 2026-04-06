package com.albeepoint.api.biz.cont.controller;

import com.albeepoint.core.biz.cont.models.entity.ContOrgEntity;
import com.albeepoint.api.biz.cont.models.vo.request.ContOrgListRegReqVo;
import com.albeepoint.api.biz.cont.models.vo.request.ContOrgListReqVo;
import com.albeepoint.api.biz.cont.models.vo.request.ContOrgRegReqVo;
import com.albeepoint.api.biz.cont.models.vo.request.ContOrgReqVo;
import com.albeepoint.api.biz.cont.models.vo.response.ContOrgResVo;
import com.albeepoint.core.biz.cont.service.ContOrgService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.http.HttpServletRequest;

import static com.albeepoint.core.common.models.AlbeeConst.okCd;
import static com.albeepoint.core.common.models.AlbeeConst.okMsg;


@RestController
@EnableWebMvc
@RequiredArgsConstructor
@RequestMapping(value = "/cont/org", produces="application/json;charset=UTF-8")
@Tag(name = "계약 기관 관리", description = "포인트 서비스를 위한 계약 기관 관리 API")
public class ContOrgController {
    @Autowired
    ContOrgService contOrgService;
    @Autowired
    TrLogService trLog;

    public static final Logger log = LogManager.getLogger(ContOrgController.class);

    @Operation(summary = "계약 기관 등록", description = "계약 기관 지점 등록")
    @PostMapping("/reg")
    public ResponseEntity<ResponseDto<ResultListDto<ContOrgResVo>>> regContOrg(
            HttpServletRequest request,
            @Parameter(description = "계약 기관 등록", required = true) @RequestBody ContOrgListRegReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        ResultListDto<ContOrgEntity> resultList = contOrgService.regContOrg(reqVo.getIVo());

        ResultListDto<ContOrgResVo> resVo = ComUtil.objectResultListCopy(resultList, ContOrgResVo.class);
        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

    @Operation(summary = "계약 기관 수정", description = "계약 관련 기관 수정")
    @PostMapping("/mod")
    public ResponseEntity<ResponseDto<ContOrgResVo>> modContOrg(
            HttpServletRequest request,
            @Parameter(description = "계약 기관 수정", required = true) @RequestBody ContOrgRegReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        ContOrgEntity result = contOrgService.modContOrg(reqVo.getIVo());

        ContOrgResVo resVo = (ContOrgResVo)ComUtil.objectCopy(result, ContOrgResVo.class);
        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

    @Operation(summary = "계약 기관 조회", description = "계약 관련 기관 조회")
    @PostMapping("")
    public ResponseEntity<ResponseDto<ContOrgResVo>> getContOrg(
            HttpServletRequest request,
            @Parameter(description = "계약 기관 조회", required = true) @RequestBody ContOrgReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        ContOrgEntity result = contOrgService.getContOrg(reqVo.getContOrgSearch());

        ContOrgResVo resVo = (ContOrgResVo)ComUtil.objectCopy(result, ContOrgResVo.class);
        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

    @Operation(summary = "계약 기관 목록 조회", description = "계약 관련 기관 목록 조회")
    @PostMapping("/list")
    public ResponseEntity<ResponseDto<ResultListDto<ContOrgResVo>>> getContOrgList(
            HttpServletRequest request,
            @Parameter(description = "계약 기관 목록 조회", required = true) @RequestBody ContOrgListReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        ResultListDto<ContOrgEntity> resultList = contOrgService.getContOrgList(reqVo.getContOrgSearch());

        ResultListDto<ContOrgResVo> resVo = ComUtil.objectResultListCopy(resultList, ContOrgResVo.class);
        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

}
