package com.albee.albeepoint.mapper.contract;
 
import org.springframework.stereotype.Component;

import com.albee.albeepoint.api.contract.dto.ContRelDto;
import com.albee.albeepoint.api.contract.dto.ContRelSearchDto;

import java.util.List;

@Component
public interface ContRelMapper {
    Long selectNewContRelNo();

    Long selectNewContRelSeq(ContRelSearchDto dom);

    ContRelDto selectContRel(ContRelSearchDto dom);

    Long selectContRelListTotalCnt(ContRelSearchDto dom);

    List<ContRelDto> selectContRelList(ContRelSearchDto dom);
}
