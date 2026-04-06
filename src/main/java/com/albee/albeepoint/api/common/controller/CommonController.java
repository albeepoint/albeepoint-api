package com.albee.albeepoint.api.common.controller;
 
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

import com.albee.albeepoint.api.common.constant.AlbeeConst;
import com.albee.albeepoint.api.common.dto.EncDto;
import com.albee.albeepoint.api.common.dto.ResponseDto;
import com.albee.albeepoint.api.common.dto.TrLogDto;
import com.albee.albeepoint.api.common.service.TrLogService;
import com.albee.albeepoint.api.util.EncUtil;
import com.albee.albeepoint.api.util.VdUtil;

import jakarta.servlet.http.HttpServletRequest;
 
@Log4j2
@RestController
@EnableWebMvc
@RequiredArgsConstructor
@RequestMapping(value = "/common", produces="application/json;charset=UTF-8")
public class CommonController {

    @Autowired
    IpFilterService ipFilterService;
    @Autowired
    UriMstService uriMstService;
    @Autowired
    TrLogService trLog; 

    @PostMapping("/enc-info")
    public ResponseEntity<ResponseDto<EncDto>> getEncInfo(
            HttpServletRequest request,
            @RequestBody EncDto dom
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, dom);
        if(VdUtil.isNotEmpty(dom.getEncStr())){
            dom.setDecStr(EncUtil.decryptAes256ByOrgCd(dom.getEncStr(), dom.getOrgCd()));
        }

        if(VdUtil.isNotEmpty(dom.getDecStr())){
            dom.setEncStr(EncUtil.encryptAes256ByOrgCd(dom.getDecStr(), dom.getOrgCd()));
        }

        trLog.modTrLog(trLogDto, dom);
        return ResponseEntity.ok().body(new ResponseDto<>(AlbeeConst.okCd, AlbeeConst.okMsg, dom));
    }

    @PostMapping("/ip/reg")
    public ResponseEntity<ResponseDto<IpFilterEntity>> regIpAddr(
            HttpServletRequest request,
            @RequestBody IpFilterDto dom
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, dom);
        IpFilter ipFilterEntity =  ipFilterService.regIpFilter(dom);

        trLog.modTrLog(trLogDto, ipFilterEntity);
        return ResponseEntity.ok().body(new ResponseDto<>(AlbeeConst.okCd, AlbeeConst.okMsg, ipFilterEntity));
    }


    @PostMapping("/uri/reg")
    public ResponseEntity<ResponseDto<UriMstEntity>> regUriReg(
            HttpServletRequest request,
            @RequestBody UriMstDto dom
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, dom);
        UriMstEntity uriMstEntity =  uriMstService.regUriMst(dom);

        trLog.modTrLog(trLogDto, uriMstEntity);
        return ResponseEntity.ok().body(new ResponseDto<>(AlbeeConst.okCd, AlbeeConst.okMsg, uriMstEntity));
    }

}
