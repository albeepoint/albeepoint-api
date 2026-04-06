package com.albee.albeepoint.api.common;

 
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.albee.albeepoint.api.common.constant.AlbeeConst;
import com.albee.albeepoint.api.common.dto.EncDto;
import com.albee.albeepoint.api.common.dto.IpFilterDto;
import com.albee.albeepoint.api.common.dto.ResponseDto;
import com.albee.albeepoint.api.common.dto.TrLogDto;
import com.albee.albeepoint.api.common.service.TrLogService;
import com.albee.albeepoint.api.util.EncUtil;
import com.albee.albeepoint.api.util.VdUtil;
import com.albee.albeepoint.mapper.base.t_ip_filter.TIpFilter;
import com.albee.albeepoint.mapper.base.t_uri_mst.TUriMst; 

import jakarta.servlet.http.HttpServletRequest;
 

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

    public static final Logger log = LogManager.getLogger(CommonController.class);

    // 암호화/복호화 정보 조회
    @PostMapping("/enc-info")
    public ResponseEntity<ResponseDto<EncDto>> getEncInfo(
            HttpServletRequest request,
            @RequestBody EncDto dom
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, dom);
        EncDto resDom = new EncDto();
        BeanUtils.copyProperties(dom, resDom);

        if(VdUtil.isNotEmpty(dom.getEncStr())){
            resDom.setDecStr(EncUtil.decryptAes256ByOrgCd(dom.getEncStr(), dom.getOrgCd()));
        }

        if(VdUtil.isNotEmpty(dom.getDecStr())){
            resDom.setEncStr(EncUtil.encryptAes256ByOrgCd(dom.getDecStr(), dom.getOrgCd()));
        }

        trLog.modTrLog(trLogDto, resDom);
        return ResponseEntity.ok().body(new ResponseDto<>(AlbeeConst.okCd, AlbeeConst.okMsg, resDom));
    }

    @PostMapping("/ip/reg")
    public ResponseEntity<ResponseDto<TIpFilter>> regIpAddr(
            HttpServletRequest request,
            @RequestBody IpFilterDto dom
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, dom);
        TIpFilter TIpFilter =  ipFilterService.regIpFilter(dom);

        trLog.modTrLog(trLogDto, TIpFilter);
        return ResponseEntity.ok().body(new ResponseDto<>(AlbeeConst.okCd, AlbeeConst.okMsg, TIpFilter));
    }


    @PostMapping("/uri/reg")
    public ResponseEntity<ResponseDto<TUriMst>> regUriReg(
            HttpServletRequest request,
            @RequestBody UriMstDto dom
    ){
        TrLogDto trLogDto = trLog.regTrLog(request, dom);
        TUriMst uriMstEntity =  uriMstService.regUriMst(dom);

        trLog.modTrLog(trLogDto, uriMstEntity);
        return ResponseEntity.ok().body(new ResponseDto<>(AlbeeConst.okCd, AlbeeConst.okMsg, uriMstEntity));
    }

}
