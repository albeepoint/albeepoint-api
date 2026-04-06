package com.albee.albeepoint.api.org.service;
 
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albee.albeepoint.api.common.constant.ErrorCode;
import com.albee.albeepoint.api.common.dto.ResultListDto;
import com.albee.albeepoint.api.org.constant.EnumOrg;
import com.albee.albeepoint.api.org.dto.OrgSearchDto;
import com.albee.albeepoint.api.util.VdUtil;
import com.albee.albeepoint.mapper.base.t_brch_mst.TBrchMst;
import com.albee.albeepoint.mapper.base.t_brch_mst.TBrchMstExample;
import com.albee.albeepoint.mapper.base.t_brch_mst.TBrchMstMapper;
import com.albee.albeepoint.mapper.base.t_org_mst.TOrgMst;
import com.albee.albeepoint.mapper.org.BrchMstMapper;

import java.util.List;
 
@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class BrchMstService {
    @Autowired
    private OrgMstService orgService;
    @Autowired
    private TBrchMstMapper tBrchMstMapper;
    @Autowired
    private BrchMstMapper brchMapper;

    /* 지점 정보 등록 */
    public void regBrchMst(TBrchMst brchEty) {
        TOrgMst orgEty = this.orgService.getOrgMstWec(new OrgSearchDto(brchEty.getOrgCd()));
        brchEty.setOrgNo(orgEty.getOrgNo());

        VdUtil.emptyEc(brchEty.getBrchNm(), ErrorCode.BIZ_ERR_001121);

        if(brchEty.getBrchDispNm() == null){
            brchEty.setBrchDispNm(brchEty.getBrchNm());
        }

        if(brchEty.getBrchStsCd() == null){
            brchEty.setBrchStsCd(EnumOrg.BrchSts.NORMAL.toString());
        }

        OrgSearchDto orgSearchDto = new OrgSearchDto();
        orgSearchDto.setOrgNo(orgEty.getOrgNo());
        brchEty.setBrchNo(brchMapper.selectSeqBrchMstNo(orgSearchDto));
        VdUtil.ec(this.tBrchMstMapper.insert(brchEty) <= 0, ErrorCode.BIZ_ERR_001141);
    }

    public void modBrchMst(TBrchMst brchEty) {
        // TOrgMst org = this.orgService.getOrgMstWec(new OrgSearchDto(brchEty.getOrgCd()));
        TBrchMst brch = this.brchMapper.selectBrchMst(new OrgSearchDto(brchEty.getOrgCd(), brchEty.getBrchCd()));
        brchEty.setOrgNo(brch.getOrgNo());
        brchEty.setBrchNo(brch.getBrchNo());

        VdUtil.ec(this.tBrchMstMapper.updateByPrimaryKeySelective(brchEty) <= 0, ErrorCode.BIZ_ERR_001142);
    }


    public TBrchMst getBrchMstWec(OrgSearchDto orgSearch) {
        TBrchMst brchEty = this.getBrchMst(orgSearch);
        VdUtil.emptyEc(brchEty, ErrorCode.BIZ_ERR_001012);
        return brchEty;
    }

    public TBrchMst getBrchMst(OrgSearchDto dom) {
        TBrchMstExample example = new TBrchMstExample();
        TBrchMstExample.Criteria criteria = example.createCriteria();

        // 2. 파라미터 체크 및 조건 추가 (XML의 <if> 역할)
        if (dom.getOrgNo() != null) {
            criteria.andOrgNoEqualTo(dom.getOrgNo());
        }
        
        if (dom.getBrchNo() != null) {
            criteria.andBrchNoEqualTo(dom.getBrchNo());
        }
        
        if (dom.getOrgCd() != null && !dom.getOrgCd().isEmpty()) {
            criteria.andOrgCdEqualTo(dom.getOrgCd());
        }
        
        if (dom.getBrchCd() != null && !dom.getBrchCd().isEmpty()) {
            criteria.andBrchCdEqualTo(dom.getBrchCd());
        }

        List<TBrchMst> list = this.tBrchMstMapper.selectByExample(example);
    
        // 첫 번째 데이터만 반환 (없으면 null)
        return list.isEmpty() ? null : list.get(0);
    }

    public ResultListDto<TBrchMst> getBrchMstList(OrgSearchDto orgSearch) {
        ResultListDto<TBrchMst> result = new ResultListDto<>();
        Long totalCnt = brchMapper.selectBrchMstListTotalCnt(orgSearch);
        result.setTotalCnt(totalCnt != null ? totalCnt : 0L);
        if(result.getTotalCnt() > 0){
            List<TBrchMst> list = brchMapper.selectBrchMstList(orgSearch);
            result.setList(list);
            result.setPageCnt(list != null && list.size() > 0 ? list.size() : 0);
        }
        return result;
    }
}
