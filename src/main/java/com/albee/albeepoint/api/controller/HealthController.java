package com.albee.albeepoint.api.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.albee.albeepoint.mapper.base.cont_mst.ContMst;
import com.albee.albeepoint.mapper.base.cont_mst.ContMstMapper;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api")
public class HealthController {

    @Autowired
    private ContMstMapper contMstMapper;


    @GetMapping("/health")
    public Map<String, Object> health() {
        ContMst contMst = this.getCustMst(); // 데이터베이스 연결 확인
        log.info("Health check - ContMst: {}", contMst);

        return Map.of(
                "status", "UP",
                "service", "albeepoint-api",
                "timestamp", LocalDateTime.now().toString()
        );
    }

    private ContMst getCustMst() {
        ContMst contMst = this.contMstMapper.selectByPrimaryKey(201006L);
        return contMst != null ? contMst : new ContMst();
    }
}
