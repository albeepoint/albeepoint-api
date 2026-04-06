package com.albee.albeepoint.mapper.common;
 
import org.springframework.stereotype.Component;

import com.albee.albeepoint.api.common.dto.EncInfoSearchDto;
import com.albee.albeepoint.api.common.dto.EncInfoDto;

import java.util.List;

@Component
public interface EncInfoMapper{ 

    Long selectEncInfoListTotalCnt(EncInfoSearchDto dom);

    List<EncInfoDto> selectEncInfoList(EncInfoSearchDto dom);
}
