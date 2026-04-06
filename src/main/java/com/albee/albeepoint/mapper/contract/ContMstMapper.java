package com.albee.albeepoint.mapper.contract;
 
import org.apache.ibatis.annotations.Mapper;

import com.albee.albeepoint.api.contract.dto.ContSearchDto;
import com.albee.albeepoint.mapper.base.t_cont_mst.TContMst;

import java.util.List;

@Mapper
public interface ContMstMapper{
    
    Long selectContMstListTotalCnt(ContSearchDto dom);

    List<TContMst> selectContMstList(ContSearchDto dom);
}
