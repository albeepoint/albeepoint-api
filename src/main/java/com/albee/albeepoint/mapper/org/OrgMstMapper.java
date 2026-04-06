package com.albee.albeepoint.mapper.org;
 
import org.apache.ibatis.annotations.Mapper;

import com.albee.albeepoint.api.org.dto.OrgSearchDto;
import com.albee.albeepoint.mapper.base.t_org_mst.TOrgMst;

import java.util.List;

@Mapper
public interface OrgMstMapper {

    // TOrgMst selectOrgMst(OrgSearchDto dom);

    Long selectOrgMstListTotalCnt(OrgSearchDto dom);

    List<TOrgMst> selectOrgMstList(OrgSearchDto dom);
}
