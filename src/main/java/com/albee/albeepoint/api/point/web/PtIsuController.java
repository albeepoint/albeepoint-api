package com.albeepoint.api.biz.point.controller;

import com.albeepoint.api.biz.point.models.vo.request.IsuCancelReqVo;
import com.albeepoint.api.biz.point.models.vo.request.IsuReqVo;
import com.albeepoint.api.biz.point.models.vo.response.IsuCancelResultResVo;
import com.albeepoint.api.biz.point.models.vo.response.IsuResultResVo;
import com.albeepoint.api.biz.point.models.vo.response.MemberPtResVo;
import com.albeepoint.core.biz.point.models.ivo.response.IsuCancelResultResIVo;
import com.albeepoint.core.biz.point.models.ivo.response.IsuResultResIVo;
import com.albeepoint.core.biz.point.models.ivo.response.MemberPtResIVo;
import com.albeepoint.core.biz.point.service.PtIsuCancelService;
import com.albeepoint.core.biz.point.service.PtIsuService;
import com.albeepoint.core.common.log.models.dto.TrLogDto;
import com.albeepoint.core.common.log.service.TrLogService;
import com.albeepoint.core.common.models.dto.ResponseDto;
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
@RequestMapping(value = "/pt", produces="application/json;charset=UTF-8")
@Tag(name = "포인트 적립 및 사용", description = "포인트 적립 및 사용 처리 API")
public class PtIsuController {
    @Autowired
    PtIsuService ptIsuService;
    @Autowired
    PtIsuCancelService ptIsuCancelService;

    @Autowired
    TrLogService trLog;

    public static final Logger log = LogManager.getLogger(PtIsuController.class);

    @Operation(summary = "포인트 적립", description = "포인트 적립 처리")
    @PostMapping("/issue")
    public ResponseEntity<ResponseDto<IsuResultResVo>> issue(
            HttpServletRequest request,
            @Parameter(description = "적립 요청", required = true) @RequestBody IsuReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        IsuResultResIVo resIVo = ptIsuService.issue(reqVo.getIVo());
        IsuResultResVo resVo = (IsuResultResVo) CommUtil.ivotovoCopy(resIVo);
        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

    @Operation(summary = "포인트 적립 취소", description = "포인트 적립 취소 처리")
    @PostMapping("/issue-cancel")
    public ResponseEntity<ResponseDto<IsuCancelResultResVo>> issueCancel(
            HttpServletRequest request,
            @Parameter(description = "적립 취소 요청", required = true) @RequestBody IsuCancelReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        IsuCancelResultResIVo resIVo = ptIsuCancelService.issueCancel(reqVo.getIVo());
        IsuCancelResultResVo resVo = (IsuCancelResultResVo) CommUtil.ivotovoCopy(resIVo);
        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }
}
