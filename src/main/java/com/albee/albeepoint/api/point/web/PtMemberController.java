package com.albee.albeepoint.api.point.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

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
import com.albee.albeepoint.api.common.dto.TrLogDto;
import com.albee.albeepoint.api.common.service.TrLogService;
import com.albee.albeepoint.api.point.dto.MemberContPtListResDto;
import com.albee.albeepoint.api.point.dto.MemberContPtReqDto;
import com.albee.albeepoint.api.point.service.MemberPtMstService;
import com.albeepoint.core.biz.point.service.TrHistDetailService;

import java.util.List;

@Log4j2
@RestController
@EnableWebMvc
@RequiredArgsConstructor
@RequestMapping(value = "/member/pt", produces="application/json;charset=UTF-8")
@Tag(name = "회원 포인트 현황 및 거래내역 조회", description = "회원 포인트 현황 및 거래내역 조회 API")
public class PtMemberController {
    @Autowired
    private MemberPtMstService memberPtMstService;
    
    @Autowired
    private TrHistDetailService trHistDetailService;
    
    @Autowired
    private TrLogService trLog;

    /*
        회원 특정 계약 포인트 현황 조회
     */
    @Operation(summary = "회원 계약 포인트 현황 조회", description = "회원 계약 포인트 현황 조회")
    @PostMapping("/cont")
    public ResponseEntity<ResponseDto<MemberContPtListResDto>> getMemberContPt(
            HttpServletRequest request,
            @Parameter(description = "회원 계약 포인트 조회", required = true) @RequestBody MemberContPtReqDto reqDto
    ){
        this.trLog.regTrLog(request, reqDto);
        MemberContPtListResDto resListDto = this.memberPtMstService.getMemberContPt(reqDto.getPtSearch());
        return ResponseEntity.ok().body(new ResponseDto<>(resListDto));
    }

    /*
        회원 거래내역 조회
     */
    @Operation(summary = "회원 거래내역 조회", description = "회원 거래내역 조회")
    @PostMapping("/history")
    public ResponseEntity<ResponseDto<ResultListDto<TrHistDetailResVo>>> getTrHistory(
            HttpServletRequest request,
            @Parameter(description = "회원 거래내역 조회", required = true) @RequestBody TrHistDetailListReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        ResultListDto<TrHistDetailResIVo> resIVo = trHistDetailService.getTrHistDetailList(reqVo.getTrHistSearch());
        ResultListDto<TrHistDetailResVo> resVo = (ResultListDto<TrHistDetailResVo>) CommUtil.ivotovoResultListCopy(resIVo);
        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }

    /*
        회원 거래내역 조회(특정 거래일련번호)
     */
    @Operation(summary = "회원 특정 거래 목록 조회", description = "회원 특정 거래번화 관련 거래이력 조회")
    @PostMapping("/history/detail")
    public ResponseEntity<ResponseDto<List<TrHistDetailResVo>>> getTrHistoryDetail(
            HttpServletRequest request,
            @Parameter(description = "특정 거래번호 거래내역 조회", required = true) @RequestBody TrSnoReqVo reqVo
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqVo);
        List<TrHistDetailResIVo> resIVo = trHistDetailService.getTrHistDetailForTrTrace(reqVo.getTrHistSearch());
        List<TrHistDetailResVo> resVo = (List<TrHistDetailResVo>) CommUtil.objectResultListCopy(resIVo, TrHistDetailResVo.class);

        trLog.modTrLog(trLogDto, resVo);
        return ResponseEntity.ok().body(new ResponseDto<>(okCd, okMsg, resVo));
    }
}
