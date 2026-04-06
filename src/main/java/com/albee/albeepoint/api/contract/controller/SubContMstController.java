package com.albeepoint.api.biz.cont.controller;

import com.albeepoint.core.biz.cont.models.entity.SubContMstEntity;
import com.albeepoint.api.biz.cont.models.vo.request.SubContListReqVo;
import com.albeepoint.api.biz.cont.models.vo.request.SubContModReqVo;
import com.albeepoint.api.biz.cont.models.vo.request.SubContRegReqVo;
import com.albeepoint.api.biz.cont.models.vo.request.SubContReqVo;
import com.albeepoint.api.biz.cont.models.vo.response.SubContResVo;
import com.albeepoint.core.biz.cont.service.SubContMstService;
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
@RequestMapping(value = "/cont/sub", produces="application/json;charset=UTF-8")
@Tag(name = "계약 관리", description = "포인트 서비스를 위한 계약 관리 API")
public class SubContMstController {
    @Autowired
    SubContMstService subContMstService;
    @Autowired
    TrLogService trLog;

    public static final Logger log = LogManager.getLogger(SubContMstController.class);

    @Operation(summary = "하위 계약 등록", description = "하위 계약 등록")
    @PostMapping("/reg")
    public ResponseEntity<ResponseDto<SubContResVo>> regContMst(
            HttpServletRequest request,
            @Parameter(description = "계약 기관 수정", required = true) @RequestBody SubContRegReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        SubContMstEntity result = subContMstService.regSubContMst(reqVo.getIVo());

        SubContResVo resVo = (SubContResVo)ComUtil.objectCopy(result, SubContResVo.class);
        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

    @Operation(summary = "하위 계약 수정", description = "하위 계약 수정")
    @PostMapping("/mod")
    public ResponseEntity<ResponseDto<SubContResVo>> modSubContMst(
            HttpServletRequest request,
            @Parameter(description = "계약 기관 수정", required = true) @RequestBody SubContModReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        SubContMstEntity result = subContMstService.modSubContMst(reqVo.getIVo());

        SubContResVo resVo = (SubContResVo)ComUtil.objectCopy(result, SubContResVo.class);
        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

    @Operation(summary = "하위 계약 조회", description = "하위 계약 조회")
    @PostMapping("")
    public ResponseEntity<ResponseDto<SubContResVo>> getContMst(
            HttpServletRequest request,
            @Parameter(description = "하위 계약 조회", required = true) @RequestBody SubContReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        SubContMstEntity result = subContMstService.getSubContMst(reqVo.getContSearch());

        SubContResVo resVo = (SubContResVo)ComUtil.objectCopy(result, SubContResVo.class);

        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

    @Operation(summary = "하위 계약 목록 조회", description = "하위 계약 목록 조회")
    @PostMapping("/list")
    public ResponseEntity<ResponseDto<ResultListDto<SubContResVo>>> getSubContMstList(
            HttpServletRequest request,
            @Parameter(description = "하위 계약 목록 조회", required = true) @RequestBody SubContListReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        ResultListDto<SubContMstEntity> resultList = subContMstService.getSubContMstList(reqVo.getContSearch());

        ResultListDto<SubContResVo> resVo = (ResultListDto)ComUtil.objectResultListCopy(resultList, SubContResVo.class);
        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

}
