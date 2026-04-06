package com.albee.albeepoint.api.config.openfeign.service;

import com.albeepoint.api.biz.point.models.vo.request.IsuReqVo;
import com.albeepoint.api.biz.point.models.vo.response.MemberPtResVo;
import com.albeepoint.api.config.openfeign.AlbeeOpenFeign;
import com.albeepoint.core.common.models.dto.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class FeignService {
    @Autowired
    private AlbeeOpenFeign albeeOpenFeign;

    public MemberPtResVo issue(IsuReqDto isuReqVo){
        ResponseEntity<ResponseDto<MemberPtResVo>> res = albeeOpenFeign.issue(isuReqVo);
        MemberPtResVo resVo = new ObjectMapper().convertValue(res.getBody().getData(), MemberPtResVo.class);
        return resVo;
    }
}
