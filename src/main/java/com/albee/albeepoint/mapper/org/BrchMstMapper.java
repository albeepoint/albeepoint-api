package com.albee.albeepoint.mapper.org;
 
import org.springframework.stereotype.Component;

import com.albee.albeepoint.api.org.dto.OrgSearchDto;
import com.albee.albeepoint.mapper.base.t_brch_mst.TBrchMst;

import java.util.List;

@Component
public interface BrchMstMapper{
    long selectSeqBrchMstNo(OrgSearchDto dom);

    // TBrchMst selectBrchMst(OrgSearchDto dom);

    Long selectBrchMstListTotalCnt(OrgSearchDto dom);

    List<TBrchMst> selectBrchMstList(OrgSearchDto dom);
}
