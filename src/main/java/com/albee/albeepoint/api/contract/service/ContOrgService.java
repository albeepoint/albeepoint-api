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
import com.albee.albeepoint.api.contract.dto.ContOrgDto;
import com.albee.albeepoint.api.contract.dto.ContOrgListRegReqDto;
import com.albee.albeepoint.api.contract.dto.ContOrgRegReqDto;
import com.albee.albeepoint.api.contract.dto.ContOrgSearchDto;
import com.albee.albeepoint.api.org.dto.OrgSearchDto;
import com.albee.albeepoint.api.org.service.OrgMstService;
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.api.util.VdUtil;
import com.albee.albeepoint.mapper.base.t_cont_mst.TContMst;
import com.albee.albeepoint.mapper.base.t_cont_mst.TContMstMapper;
import com.albee.albeepoint.mapper.base.t_cont_org.TContOrg;
import com.albee.albeepoint.mapper.base.t_cont_org.TContOrgMapper;
import com.albee.albeepoint.mapper.base.t_org_mst.TOrgMst;
import com.albee.albeepoint.mapper.contract.ContOrgMapper;

import java.util.List; 

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = {RuntimeException.class, Exception.class})
public class ContOrgService {
    @Autowired
    private TContMstMapper tContMstMapper;
    @Autowired
    private TContOrgMapper tContOrgMapper;
    @Autowired
    private ContOrgMapper contOrgMapper;
    @Autowired
    private OrgMstService orgMstService; 

    public void regContOrg(ContOrgListRegReqDto reqDto) {
        TContMst contMstEty = this.tContMstMapper.selectByPrimaryKey(reqDto.getContNo());
        VdUtil.emptyEc(contMstEty, ErrorCode.BIZ_ERR_001086, "계약정보 오류");

        for(ContOrgRegReqDto contOrg : reqDto.getContOrgList()) { 
            TOrgMst orgMstEty = orgMstService.getOrgMstWec(new OrgSearchDto(contOrg.getOrgCd()));
            VdUtil.emptyEc(orgMstEty, ErrorCode.BIZ_ERR_001008, "기관코드 오류"); 

            TContOrg contOrgEty = this.tContOrgMapper.selectByPrimaryKey(reqDto.getContNo(), orgMstEty.getOrgNo());

            int cnt = 0;
            if(contOrgEty == null){
                contOrgEty = (TContOrg)ComUtil.objectCopy(contOrg, TContOrg.class);
                contOrgEty.setContNo(reqDto.getContNo());
                contOrgEty.setOrgNo(orgMstEty.getOrgNo());
                contOrgEty.setContSeq(VdUtil.isEmpty(contOrg.getContSeq()) ? 0L : contOrg.getContSeq());
                cnt = this.tContOrgMapper.insert(contOrgEty);
            }else{
                contOrgEty.setContNo(reqDto.getContNo());
                // contOrgEty.setOrgCd(contOrg.getOrgCd());
                contOrgEty.setBrchPolicyTypeCd(contOrg.getBrchPolicyTypeCd().getCode());
                contOrgEty.setContOrgStsCd(contOrg.getContOrgStsCd().getCode());
                contOrgEty.setSaveCanYn(contOrg.getSaveCanYn());
                contOrgEty.setUseCanYn(contOrg.getUseCanYn());
                contOrgEty.setTotalIsuAblePt(contOrg.getTotalIsuAblePt());
                contOrgEty.setTotalIsuAbleCnt(contOrg.getTotalIsuAbleCnt());
                cnt = this.tContOrgMapper.updateByPrimaryKeySelective(contOrgEty);
            }

            VdUtil.ec(cnt < 1, ErrorCode.BIZ_ERR_001086, "계약-기관 등록 오류");
        }
    }

    public void modContOrg(ContOrgListRegReqDto reqDto) {
        TContMst contMstEty = this.tContMstMapper.selectByPrimaryKey(reqDto.getContNo());
        VdUtil.emptyEc(contMstEty, ErrorCode.BIZ_ERR_001086, "계약정보 오류");

        for(ContOrgRegReqDto contOrg : reqDto.getContOrgList()) { 
            TOrgMst orgMstEty = orgMstService.getOrgMstWec(new OrgSearchDto(contOrg.getOrgCd()));
            VdUtil.emptyEc(orgMstEty, ErrorCode.BIZ_ERR_001008, "기관코드 오류");

            TContOrg contOrgEty = this.tContOrgMapper.selectByPrimaryKey(reqDto.getContNo(), orgMstEty.getOrgNo());
            VdUtil.emptyEc(contOrgEty, ErrorCode.BIZ_ERR_001086, "계약-기관 정보 오류");
            contOrgEty.setContNo(reqDto.getContNo());
            contOrgEty.setBrchPolicyTypeCd(contOrg.getBrchPolicyTypeCd().getCode());
            contOrgEty.setContOrgStsCd(contOrg.getContOrgStsCd().getCode());
            contOrgEty.setSaveCanYn(contOrg.getSaveCanYn());
            contOrgEty.setUseCanYn(contOrg.getUseCanYn());
            contOrgEty.setTotalIsuAblePt(contOrg.getTotalIsuAblePt());
            contOrgEty.setTotalIsuAbleCnt(contOrg.getTotalIsuAbleCnt());
            int cnt = this.tContOrgMapper.updateByPrimaryKeySelective(contOrgEty);
            VdUtil.ec(cnt < 1, ErrorCode.BIZ_ERR_001086, "계약-기관 수정 오류");
        }
    }

    public TContOrg getContOrg(ContOrgSearchDto dom) {
        VdUtil.emptyEc(dom.getContNo(), ErrorCode.BIZ_ERR_001086, "계약번호 필수 입력");

        if(dom.getOrgNo() != null){
            return this.tContOrgMapper.selectByPrimaryKey(dom.getContNo(), dom.getOrgNo());
        }else if(dom.getOrgCd() != null){
            TOrgMst orgMstEty = this.orgMstService.getOrgMstWec(new OrgSearchDto(dom.getOrgCd()));  
            VdUtil.emptyEc(orgMstEty, ErrorCode.BIZ_ERR_001008, "기관코드 오류");
            return this.tContOrgMapper.selectByPrimaryKey(dom.getContNo(), orgMstEty.getOrgNo());
        }

        return null;
    }

    public TContOrg getContOrgWec(ContOrgSearchDto dom) {
        TContOrg contOrgEty = this.getContOrg(dom);
        VdUtil.emptyEc(contOrgEty, ErrorCode.BIZ_ERR_001102, "계약-기관 정보 오류");
        return contOrgEty;
    }

    public ResultListDto<ContOrgDto> getContOrgList(ContOrgSearchDto dom) {
        ResultListDto<ContOrgDto> result = new ResultListDto<>();
        Long totalCnt = this.contOrgMapper.selectContOrgListTotalCnt(dom);
        result.setTotalCnt(totalCnt != null ? totalCnt : 0L);
        if(result.getTotalCnt() > 0){
            List<ContOrgDto> list = this.contOrgMapper.selectContOrgList(dom);
            result.setList(list);
            result.setPageCnt(list != null && list.size() > 0 ? list.size() : 0);
        }
        return result;
    }

}
