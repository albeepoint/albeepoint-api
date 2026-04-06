package com.albee.albeepoint.mapper.contract;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ContBrchMapper {
    int insertContBrch(ContBrchEntity dom);

    int updateContBrch(ContBrchEntity dom);

    int deleteContBrch(ContBrchEntity dom);

    ContBrchEntity selectContBrch(ContOrgSearch dom);

    Long selectContBrchListTotalCnt(ContOrgSearch dom);

    List<ContBrchEntity> selectContBrchList(ContOrgSearch dom);
}
