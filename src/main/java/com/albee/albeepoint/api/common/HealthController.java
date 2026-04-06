package com.albee.albeepoint.api.common;

import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.albee.albeepoint.mapper.base.t_cont_mst.TContMst;
import com.albee.albeepoint.mapper.base.t_cont_mst.TContMstMapper; 

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/api")
public class HealthController {

    @Autowired
    private TContMstMapper contMstMapper;


    @GetMapping("/health")
    public Map<String, Object> health() {
        TContMst contMst = this.getContMst(); // 데이터베이스 연결 확인
        log.info("Health check - ContMst: {}", contMst);

        return Map.of(
                "status", "UP",
                "service", "albeepoint-api",
                "timestamp", LocalDateTime.now().toString(),
                "contMst", contMst
        );
    }

    private TContMst getContMst() {
        TContMst contMst = this.contMstMapper.selectByPrimaryKey(201006L);
        return contMst != null ? contMst : new TContMst();
    }
}
