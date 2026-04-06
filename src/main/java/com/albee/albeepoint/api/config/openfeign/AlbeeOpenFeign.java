package com.albee.albeepoint.api.config.openfeign;
 
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.albee.albeepoint.api.common.dto.ResponseDto;


@FeignClient(name = "albee", url = "${feignUrl.albeeRoot}", configuration = OpenFeignConfig.class)
public interface AlbeeOpenFeign {

    @RequestMapping(method = RequestMethod.POST, value = "/pt/issue")
    ResponseEntity<ResponseDto<MemberPtResVo>> issue(@RequestBody IsuReqDto reqVo);
}
