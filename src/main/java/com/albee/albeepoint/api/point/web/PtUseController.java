package com.albeepoint.api.biz.point.controller;

import com.albeepoint.api.biz.point.models.vo.request.AbleReqVo;
import com.albeepoint.api.biz.point.models.vo.request.UseCancelReqVo;
import com.albeepoint.api.biz.point.models.vo.request.UseReqVo;
import com.albeepoint.api.biz.point.models.vo.response.*;
import com.albeepoint.api.biz.point.service.PtUseAfterProcessService;
import com.albeepoint.core.biz.point.models.ivo.response.AbleResultResIVo;
import com.albeepoint.core.biz.point.models.ivo.response.UseCancelResultResIVo;
import com.albeepoint.core.biz.point.models.ivo.response.UseResultResIVo;
import com.albeepoint.core.biz.point.service.PtUseCancelService;
import com.albeepoint.core.biz.point.service.PtUseService;
import com.albeepoint.core.common.log.models.dto.TrLogDto;
import com.albeepoint.core.common.log.service.TrLogService;
import com.albeepoint.core.common.models.dto.ResponseDto;
import com.albeepoint.core.common.util.CommUtil;
import com.albeepoint.core.common.util.VdUtil;
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

import java.util.List;

import static com.albeepoint.core.common.models.AlbeeConst.okCd;
import static com.albeepoint.core.common.models.AlbeeConst.okMsg;


@RestController
@EnableWebMvc
@RequiredArgsConstructor
@RequestMapping(value = "/pt", produces="application/json;charset=UTF-8")
@Tag(name = "포인트 적립 및 사용", description = "포인트 적립 및 사용 처리 API")
public class PtUseController {
    @Autowired
    PtUseService ptUseService;

    @Autowired
    PtUseCancelService ptUseCancelService;

    @Autowired
    PtUseAfterProcessService afterProcessService;

    @Autowired
    TrLogService trLog;

    public static final Logger log = LogManager.getLogger(PtUseController.class);

    @Operation(summary = "사용 가능 포인트 조회", description = "포인트 사용 전 사용 가능한 포인트 조회")
    @PostMapping("/able")
    public ResponseEntity<ResponseDto<AbleResultResVo>> useAble(
            HttpServletRequest request,
            @Parameter(description = "사용 가능 포인트 조회", required = true) @RequestBody AbleReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        AbleResultResIVo resIVo = ptUseService.getAble(reqVo.getIVo());
        AbleResultResVo resVo = (AbleResultResVo) CommUtil.ivotovoCopy(resIVo);

        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

    @Operation(summary = "포인트 사용", description = "포인트 사용 처리")
    @PostMapping("/use")
    public ResponseEntity<ResponseDto<UseResultResVo>> use(
            HttpServletRequest request,
            @Parameter(description = "포인트 사용 요청", required = true) @RequestBody UseReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        UseResultResIVo resIVo = ptUseService.use(reqVo.getIVo(), true);
        UseResultResVo resVo = (UseResultResVo) CommUtil.ivotovoCopy(resIVo);
        resVo.setUsedList((List<UsedPtResVo>) CommUtil.ivotovoListCopy(resIVo.getUsedList()));

        if(VdUtil.isNotEmpty(resVo.getTrSno())) {
            try {
                log.info("사용 후 추가 처리 시작");
                afterProcessService.useAfterProcess(reqVo, resVo);
                log.info("사용 후 추가 처리 종료");
            } catch (Exception e) {
                log.error("사용 후 추가 처리 중 에러 : " + e.getMessage());
            }
        }
        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

    @Operation(summary = "포인트 사용 취소", description = "포인트 사용 취소 처리")
    @PostMapping("/use-cancel")
    public ResponseEntity<ResponseDto<UseCancelResultResVo>> useCancel(
            HttpServletRequest request,
            @Parameter(description = "포인트 사용 요청", required = true) @RequestBody UseCancelReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        UseCancelResultResIVo resIVo = ptUseCancelService.useCancel(reqVo.getIVo());
        UseCancelResultResVo resVo = (UseCancelResultResVo) CommUtil.ivotovoCopy(resIVo);
        resVo.setUseCanceledList((List<UseCanceledPtResVo>) CommUtil.ivotovoListCopy(resIVo.getUseCanceledList()));

        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }
}
