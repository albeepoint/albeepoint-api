package com.albee.albeepoint.mapper.point;
 
import org.springframework.stereotype.Component;

import com.albee.albeepoint.api.point.dto.TrHistDto;
import com.albee.albeepoint.api.point.dto.TrHistSearchDto;

import java.util.List;

@Component
public interface TrHistMapper{

    TrHistDto selectLastOneTrHist(TrHistSearchDto dom);

    Long selectTrHistListTotalCnt(TrHistSearchDto dom);

    List<TrHistDto> selectTrHistList(TrHistSearchDto dom);
}
