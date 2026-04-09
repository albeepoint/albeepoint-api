package com.albee.albeepoint.mapper.contract;

import org.springframework.stereotype.Component;

import com.albee.albeepoint.api.contract.dto.ContBrchDto;
import com.albee.albeepoint.api.contract.dto.ContOrgSearchDto;

import java.util.List;

@Component
public interface ContBrchMapper {

    ContBrchDto selectContBrch(ContOrgSearchDto dom);

    Long selectContBrchListTotalCnt(ContOrgSearchDto dom);

    List<ContBrchDto> selectContBrchList(ContOrgSearchDto dom);
}
