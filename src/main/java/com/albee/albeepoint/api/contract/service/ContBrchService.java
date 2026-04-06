package com.albee.albeepoint.api.contract.service;
 
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albee.albeepoint.api.common.constant.ErrorCode;
import com.albee.albeepoint.api.common.dto.ResultListDto;
import com.albee.albeepoint.api.contract.dto.ContBrchDto;
import com.albee.albeepoint.api.contract.dto.ContBrchRegReqDto;
import com.albee.albeepoint.api.contract.dto.ContOrgSearchDto;
import com.albee.albeepoint.api.org.dto.OrgSearchDto;
import com.albee.albeepoint.api.org.service.BrchMstService;
import com.albee.albeepoint.api.org.service.OrgMstService;
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.api.util.VdUtil;
import com.albee.albeepoint.mapper.base.t_brch_mst.TBrchMst;
import com.albee.albeepoint.mapper.base.t_cont_brch.TContBrch;
import com.albee.albeepoint.mapper.base.t_cont_brch.TContBrchMapper;
import com.albee.albeepoint.mapper.base.t_org_mst.TOrgMst;

import java.util.List;
 

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class ContBrchService {
    @Autowired
    private TContBrchMapper tContBrchMapper;

    @Autowired
    private ContBrchMapper contBrchMapper;

    @Autowired
    private OrgMstService orgMstService;

    @Autowired
    private BrchMstService brchMstService;

    public void regContBrch(List<ContBrchRegReqDto> reqDto) { 
        for(ContBrchRegReqDto dto : reqDto) {
            TOrgMst org = this.orgMstService.getOrgMstWec(new OrgSearchDto(dto.getOrgCd()));
            VdUtil.emptyEc(org, ErrorCode.BIZ_ERR_001008, "기관코드 오류");
            TBrchMst brch = this.brchMstService.getBrchMstWec(new OrgSearchDto(dto.getOrgCd(), dto.getBrchCd()));
            VdUtil.emptyEc(brch, ErrorCode.BIZ_ERR_001121, "지점코드 오류");

            TContBrch contBrchEty = (TContBrch)ComUtil.objectCopy(dto, TContBrch.class);
            contBrchEty.setOrgNo(org.getOrgNo());
            contBrchEty.setBrchNo(brch.getBrchNo());
            VdUtil.ec(this.tContBrchMapper.insert(contBrchEty) <= 0, ErrorCode.BIZ_ERR_001086, "계약-지점 인서트 오류");
        } 
    }

    public ResultListDto<ContBrchDto> getContBrchList(ContOrgSearchDto dom) {
        ResultListDto<ContBrchDto> result = new ResultListDto<>();
        Long totalCnt = contBrchMapper.selectContBrchListTotalCnt(dom);
        result.setTotalCnt(totalCnt != null ? totalCnt : 0L);
        if(result.getTotalCnt() > 0){
            List<ContBrchDto> list = contBrchMapper.selectContBrchList(dom);
            result.setList(list);
            result.setPageCnt(list != null && list.size() > 0 ? list.size() : 0);
        }
        return result;
    }

}
