package com.albee.albeepoint.api.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.albee.albeepoint.mapper")
public class MyBatisConfig {
}
