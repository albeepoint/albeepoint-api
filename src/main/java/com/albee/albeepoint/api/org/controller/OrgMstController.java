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
 
import com.albee.albeepoint.api.common.constant.ErrorCode;
import com.albee.albeepoint.api.common.dto.ResponseDto;
import com.albee.albeepoint.api.common.dto.ResultListDto;
import com.albee.albeepoint.api.common.dto.TrLogDto;
import com.albee.albeepoint.api.common.exception.AlbeepointException;
import com.albee.albeepoint.api.common.service.TrLogService;
import com.albee.albeepoint.api.org.dto.OrgCdReqDto; 
import com.albee.albeepoint.api.org.dto.OrgListReqDto; 
import com.albee.albeepoint.api.org.dto.OrgReqDto;
import com.albee.albeepoint.api.org.dto.OrgResDto;
import com.albee.albeepoint.api.org.service.OrgMstService;
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.mapper.base.t_org_mst.TOrgMst;
 
 
@Log4j2
@RestController
@EnableWebMvc
@RequiredArgsConstructor
@RequestMapping(value = "/org", produces="application/json;charset=UTF-8")
@Tag(name = "기관 및 지점 관리", description = "기관 및 지점 등록/수정 API")
public class OrgMstController {
    @Autowired
    private OrgMstService orgMstService;
    @Autowired
    private TrLogService trLog; 

    /* 기관 정보 등록 */
    @Operation(summary = "기관 정보 등록", description = "기관 정보 등록")
    @PostMapping("/reg")
    public ResponseEntity<ResponseDto> regOrgMst(
            HttpServletRequest request,
            @Parameter(description = "기관 정보 등록", required = true) @RequestBody OrgReqDto reqDto
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqDto);
        try{
            this.orgMstService.regOrgMst(reqDto.getEntity());
        }catch(AlbeepointException e){
            return ResponseEntity.ok().body(new ResponseDto<>(e));
        }

        trLog.modTrLog(trLogDto, reqDto);
        return ResponseEntity.ok().body(new ResponseDto<>());
    }

    /* 기관 정보 수정 */
    @Operation(summary = "기관 정보 수정", description = "기관 정보 수정")
    @PostMapping("/mod")
    public ResponseEntity<ResponseDto> modOrgMst(
            HttpServletRequest request,
            @Parameter(description = "기관 정보 수정", required = true) @RequestBody OrgReqDto reqDto
    ){
        trLog.regTrLog(request, reqDto); 

        try{
            this.orgMstService.modOrgMst(reqDto.getEntity());
        }catch(AlbeepointException e){
            return ResponseEntity.ok().body(new ResponseDto<>(e));
        } 
        return ResponseEntity.ok().body(new ResponseDto<>());
    }

    /* 기관 정보 조회 */
    @Operation(summary = "기관 조회", description = "기관 조회")
    @PostMapping("")
    public ResponseEntity<ResponseDto<OrgResDto>> getOrgMst(
            HttpServletRequest request,
            @Parameter(description = "기관 정보 조회", required = true) @RequestBody OrgCdReqDto reqDto
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqDto);
        TOrgMst orgMst = orgMstService.getOrgMst(reqDto.getOrgSearch());

        if(orgMst == null){
            return ResponseEntity.ok().body(new ResponseDto<>(ErrorCode.BIZ_ERR_001011));
        }

        OrgResDto resDto = (OrgResDto) ComUtil.objectCopy(orgMst, OrgResDto.class);
        trLog.modTrLog(trLogDto, resDto);
        return ResponseEntity.ok().body(new ResponseDto<>(resDto));
    }

    /* 기관 목록 조회 */
    @Operation(summary = "기관 목록 조회", description = "기관 목록 조회")
    @PostMapping("/list")
    public ResponseEntity<ResponseDto<ResultListDto<OrgResDto>>> getOrgMstList(
            HttpServletRequest request,
            @Parameter(description = "기관 정보 조회", required = true) @RequestBody OrgListReqDto reqDto
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, reqDto);
        ResultListDto<TOrgMst> result = orgMstService.getOrgMstList(reqDto.getOrgSearch());

        ResultListDto<OrgResDto> resDto = ComUtil.objectResultListCopy(result, OrgResDto.class);
        trLog.modTrLog(trLogDto, resDto);
        return ResponseEntity.ok().body(new ResponseDto<>(resDto));
    }

}
