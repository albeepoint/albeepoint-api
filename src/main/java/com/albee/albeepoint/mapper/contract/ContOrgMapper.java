package com.albee.albeepoint.mapper.contract;
 
import org.springframework.stereotype.Component;

import com.albee.albeepoint.api.contract.dto.ContOrgDto;
import com.albee.albeepoint.api.contract.dto.ContOrgSearchDto;

import java.util.List;

@Component
public interface ContOrgMapper{

    ContOrgDto selectContOrg(ContOrgSearchDto dom);

    Long selectContOrgListTotalCnt(ContOrgSearchDto dom);

    List<ContOrgDto> selectContOrgList(ContOrgSearchDto dom);
}
