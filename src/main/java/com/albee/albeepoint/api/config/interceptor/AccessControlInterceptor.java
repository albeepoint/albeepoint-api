package com.albee.albeepoint.api.config.interceptor;

import com.albeepoint.core.biz.member.models.dto.MemberSearch;
import com.albeepoint.core.biz.member.models.entity.MemberMstEntity;
import com.albeepoint.core.biz.member.service.MemberMstService;
import com.albeepoint.core.common.filter.models.EnumFilter;
import com.albeepoint.core.common.filter.models.dto.IpFilterSearch;
import com.albeepoint.core.common.filter.models.dto.UriMstSearch;
import com.albeepoint.core.common.filter.models.entity.UriMstEntity;
import com.albeepoint.core.common.filter.service.IpFilterService;
import com.albeepoint.core.common.filter.service.UriMstService;
import com.albeepoint.core.common.util.CommUtil;
import com.albeepoint.core.common.util.VdUtil;
import com.albeepoint.core.common.util.WebUtil;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static com.albeepoint.core.common.models.ErrorCode.BIZ_ERR_001130;

@RequiredArgsConstructor
@Component
public class AccessControlInterceptor implements HandlerInterceptor {

    @Autowired
    IpFilterService ipFilterService;
    @Autowired
    UriMstService uriMstService;
    @Autowired
    MemberMstService memberMstService;

    public static final Logger log = LogManager.getLogger(AccessControlInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String inputJsonString = new String(IOUtils.toByteArray(request.getInputStream()));
        log.info("inputJsonString=[" + inputJsonString + "]");
        String ipAddr = WebUtil.getClientIp(request);    // 추가

        String url = request.getRequestURI() == null ? null : request.getRequestURI().substring(4, request.getRequestURI().length() - 1);
        if (VdUtil.isNotEmpty(request.getRequestURI())
                && url.length() >= 9
                && (url.substring(0, 8).equals("/swagger")
                || url.substring(0, 9).equals("/api-docs")
                || url.substring(0, 9).equals("/v3/api-d"))) {
            return true;
        }

        UriMstSearch uriMstSearch = new UriMstSearch(request.getRequestURI());
        changeMemberLastContactDt(inputJsonString);


        // uriMstSearch.setTrGbCd(EnumFilter.UriTrGb.MGMT);
        uriMstSearch.setUseYn("Y");
        UriMstEntity uriMstEntity = uriMstService.getUriMst(uriMstSearch);

        // 등록된 uri 가 없거나 사용여부가 Y 가 아니면 false
        VdUtil.ec((VdUtil.isEmpty(uriMstEntity)
                || !uriMstEntity.getUseYn().equalsIgnoreCase("Y")), BIZ_ERR_001130);

        // 접근정책이 ALL 이면 무조건 통과
        if (VdUtil.isEqual(uriMstEntity.getAcsPolicyCd(), EnumFilter.AcsPolicy.ALL)) {
            return true;
        }

        // log.info("IpAddressAccessControlInterceptor.preHandle :: client-ip = [" + ipAddr + "]");

        // 접근정책이 화이트리스트 이면 등록된 IP 만 통과
        if (VdUtil.isEqual(uriMstEntity.getAcsPolicyCd(), EnumFilter.AcsPolicy.WHITE)
                && VdUtil.isEmpty(ipFilterService.getIpFilter(new IpFilterSearch(ipAddr)))) {
            // return false;
            return true;  // 일단 무조건 통과
        }

        return true;
    }

    // 회원최종접촉일 수정
    private void changeMemberLastContactDt(String inputJson){
        inputJson = "{ data :  " + inputJson + " }";
        Object orgCdObj = ComUtil.findGsonMapByVarValue(new Gson().fromJson(inputJson, Map.class), "orgCd");
        String orgCd = (String)orgCdObj;
        log.info("inputJson : orgCd=" + orgCd);

        Object memberIdObj = ComUtil.findGsonMapByVarValue(new Gson().fromJson(inputJson, Map.class), "memberId");
        String memberId = (String)memberIdObj;
        log.info("inputJson : memberId=" + memberId);

        if(VdUtil.isNotEmpty(memberId) && VdUtil.isNotEmpty(orgCd)){
            MemberMstEntity mbrEty = memberMstService.getMemberMstByIdOrPid(new MemberSearch(orgCd, memberId));
            if(VdUtil.isNotEmpty(mbrEty)) {
                memberMstService.modMemberMstLastContact(mbrEty);
            }
        }
    }

}