package com.albee.albeepoint.api.org.controller;
 
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

import com.albee.albeepoint.api.common.constant.AlbeeConst;
import com.albee.albeepoint.api.common.dto.ResponseDto;
import com.albee.albeepoint.api.common.dto.ResultListDto;
import com.albee.albeepoint.api.common.dto.TrLogDto;
import com.albee.albeepoint.api.common.exception.AlbeepointException;
import com.albee.albeepoint.api.common.service.TrLogService;
import com.albee.albeepoint.api.org.dto.BrchCdReqDto;
import com.albee.albeepoint.api.org.dto.BrchListReqDto;
import com.albee.albeepoint.api.org.dto.BrchReqDto;
import com.albee.albeepoint.api.org.dto.BrchResDto;
import com.albee.albeepoint.api.org.service.BrchMstService;
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.mapper.base.t_brch_mst.TBrchMst;
 
@Log4j2
@RestController
@EnableWebMvc
@RequiredArgsConstructor
@RequestMapping(value = "/brch", produces="application/json;charset=UTF-8")
@Tag(name = "기관 및 지점 관리", description = "기관 및 지점 등록/수정 API")
public class BrchMstController {
    @Autowired
    private BrchMstService brchMstService;

    @Autowired
    private TrLogService trLog;

    /* 지점 정보 등록 */
    @Operation(summary = "지점 정보 등록", description = "지점 정보 등록")
    @PostMapping("/reg")
    public ResponseEntity<ResponseDto> regBrchMst(
            HttpServletRequest request,
            @Parameter(description = "지점 정보 등록", required = true) @RequestBody BrchReqDto reqDto
    ){
        trLog.regTrLog(request, reqDto);
        try{
            this.brchMstService.regBrchMst(reqDto.getEntity());
        }catch(AlbeepointException e){
            return ResponseEntity.ok().body(new ResponseDto<>(e));
        }
        
        return ResponseEntity.ok().body(new ResponseDto<>());
    }

    @Operation(summary = "지점 정보 수정", description = "지점 정보 수정")
    @PostMapping("/mod")
    public ResponseEntity<ResponseDto<BrchResDto>> modBrchMst(
            HttpServletRequest request,
            @Parameter(description = "지점 정보 수정", required = true) @RequestBody BrchReqDto reqDto
    ){
        trLog.regTrLog(request, reqDto);
        try{
            this.brchMstService.modBrchMst(reqDto.getEntity());
        }catch(AlbeepointException e){
            return ResponseEntity.ok().body(new ResponseDto<>(e));
        }

        return ResponseEntity.ok().body(new ResponseDto<>());
    }

    @Operation(summary = "지점 정보 조회", description = "지점 정보 조회")
    @PostMapping("")
    public ResponseEntity<ResponseDto<BrchResDto>> getBrchMst(
            HttpServletRequest request,
            @Parameter(description = "지점 정보 조회", required = true) @RequestBody BrchCdReqDto reqDto
    ){
        trLog.regTrLog(request, reqDto);
        TBrchMst result = this.brchMstService.getBrchMst(reqDto.getOrgSearch());

        BrchResDto resDto = (BrchResDto) ComUtil.objectCopy(result, BrchResDto.class);
        
        return ResponseEntity.ok().body(new ResponseDto<>(resDto));
    }

    @Operation(summary = "지점 목록 조회", description = "지점 목록 조회")
    @PostMapping("/list")
    public ResponseEntity<ResponseDto<ResultListDto<BrchResDto>>> getBrchMstList(
            HttpServletRequest request,
            @Parameter(description = "지점 목록 조회", required = true) @RequestBody BrchListReqDto reqDto
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqDto);
        ResultListDto<TBrchMst> result = brchMstService.getBrchMstList(reqDto.getOrgSearch());

        ResultListDto<BrchResDto> resDto = ComUtil.objectResultListCopy(result, BrchResDto.class);
        trLog.modTrLog(trLogDto, resDto);
        return ResponseEntity.ok().body(new ResponseDto<>(resDto));
    }

}
