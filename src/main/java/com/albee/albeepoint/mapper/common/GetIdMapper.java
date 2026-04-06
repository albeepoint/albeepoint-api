package com.albee.albeepoint.mapper.common;
 
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
 
 

@Component
public interface GetIdMapper{ 
    Long createContMstHistId(@Param("contNo") Long contNo);
}
