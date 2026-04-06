package com.albee.albeepoint.mapper.common;
 
import org.springframework.stereotype.Component;

import com.albee.albeepoint.api.common.dto.TrLogSearchDto;
import com.albee.albeepoint.mapper.base.t_tr_log.TTrLog;

import java.util.List;

@Component
public interface TrLogMapper{

    Long selectTrLogListTotalCnt(TrLogSearchDto dom);

    List<TTrLog> selectTrLogList(TrLogSearchDto dom);
}
