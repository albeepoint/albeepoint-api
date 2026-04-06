package com.albee.albeepoint.api.org.service;

import com.albee.albeepoint.api.common.constant.EnumCommon;
import com.albee.albeepoint.api.common.constant.ErrorCode;
import com.albee.albeepoint.api.common.dto.EncInfoDto;
import com.albee.albeepoint.api.common.dto.ResultListDto;
import com.albee.albeepoint.api.common.service.EncInfoService;
import com.albee.albeepoint.api.org.constant.EnumOrg;
import com.albee.albeepoint.api.org.dto.OrgSearchDto;
import com.albee.albeepoint.api.util.EncUtil;
import com.albee.albeepoint.api.util.VdUtil;
import com.albee.albeepoint.mapper.base.t_org_mst.TOrgMst;
import com.albee.albeepoint.mapper.base.t_org_mst.TOrgMstExample;
import com.albee.albeepoint.mapper.base.t_org_mst.TOrgMstMapper;
import com.albee.albeepoint.mapper.org.OrgMstMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.LocalDateTime;
import java.util.List;
 

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class OrgMstService {
    @Autowired
    private TOrgMstMapper tOrgMapper;
    
    @Autowired
    private OrgMstMapper orgMapper;

    @Autowired
    private EncInfoService encInfoService;

    /* 기관 마스터 등록 */
    public void regOrgMst(TOrgMst orgEty) {
        VdUtil.emptyEc(orgEty.getOrgNm(), ErrorCode.BIZ_ERR_001004);

        if(orgEty.getOrgDispNm() == null){
            orgEty.setOrgDispNm(orgEty.getOrgNm());
        }

        if(orgEty.getJoinDt() == null){
            orgEty.setJoinDt(LocalDateTime.now());
        }

        if(orgEty.getOrgStsCd() == null){
            orgEty.setOrgStsCd(EnumOrg.OrgSts.NORMAL.getCode());
        }

        VdUtil.ec(this.tOrgMapper.insert(orgEty) <= 0, ErrorCode.BIZ_ERR_001137);

        /// 암호화키 생성
        String encKey = EncUtil.getSalt();
        String salt = EncUtil.getSalt();
        EncInfoDto encInfoDto = new EncInfoDto();
        encInfoDto.setTargetGbCd(EnumCommon.EncTarget.ORG.getCode());
        encInfoDto.setTargetVal(orgEty.getOrgNo().toString());
        encInfoDto.setEncKey(encKey);
        encInfoDto.setSaltVal(salt);
        encInfoDto.setEncStsCd(EnumCommon.EncSts.NORMAL.getCode());
        encInfoService.regEncInfo(encInfoDto);
    }

    /* 기관 마스터 수정 */
    public void modOrgMst(TOrgMst orgEty) {
        VdUtil.emptyEc(orgEty.getOrgNm(), ErrorCode.BIZ_ERR_001004);

        if(orgEty.getOrgDispNm() == null){
            orgEty.setOrgDispNm(orgEty.getOrgNm());
        }

        if(orgEty.getJoinDt() == null){
            orgEty.setJoinDt(LocalDateTime.now());
        }

        if(orgEty.getOrgStsCd() == null){
            orgEty.setOrgStsCd(EnumOrg.OrgSts.NORMAL.getCode());
        }

        TOrgMst oldOrgMstEty = getOrgMstWec(new OrgSearchDto(orgEty.getOrgCd()));
        orgEty.setOrgNo(oldOrgMstEty.getOrgNo());

        VdUtil.ec(this.tOrgMapper.updateByPrimaryKeySelective(oldOrgMstEty) <= 0, ErrorCode.BIZ_ERR_001139);
    }

    /* 기관 마스터 조회 */
    public TOrgMst getOrgMst(OrgSearchDto searchDto) {
        if(searchDto.getOrgNo() != null){
            return tOrgMapper.selectByPrimaryKey(searchDto.getOrgNo());
        } else if(searchDto.getOrgCd() != null){
            TOrgMstExample example = new TOrgMstExample();
            example.createCriteria().andOrgCdEqualTo(searchDto.getOrgCd());
            return tOrgMapper.selectByExample(example).stream().findFirst().orElse(null);
        }
        return null;
    }

    /* 기관 마스터 조회 */
    public TOrgMst getOrgMstWec(OrgSearchDto orgSearch) {
        VdUtil.ec(VdUtil.isEmpty(orgSearch.getOrgNo()) && VdUtil.isEmpty(orgSearch.getOrgCd())
                , ErrorCode.BIZ_ERR_001095, "기관번호 또는 기관코드 필수 입력");
        TOrgMstExample example = new TOrgMstExample();
        TOrgMstExample.Criteria criteria = example.createCriteria();
        if(VdUtil.isNotEmpty(orgSearch.getOrgNo())){
            criteria.andOrgNoEqualTo(orgSearch.getOrgNo());
        }
        if(VdUtil.isNotEmpty(orgSearch.getOrgCd())){
            criteria.andOrgCdEqualTo(orgSearch.getOrgCd());
        }
        TOrgMst orgEty = this.tOrgMapper.selectByExample(example).stream().findFirst().orElse(null);
        VdUtil.emptyEc(orgEty, ErrorCode.BIZ_ERR_001011);
        return orgEty;
    }

    /* 기관 마스터 리스트 조회 */
    public ResultListDto<TOrgMst> getOrgMstList(OrgSearchDto orgSearch) {
        ResultListDto<TOrgMst> result = new ResultListDto<>();
        Long totalCnt = this.orgMapper.selectOrgMstListTotalCnt(orgSearch);
        result.setTotalCnt(totalCnt != null ? totalCnt : 0L);
        if(result.getTotalCnt() > 0){
            List<TOrgMst> list = this.orgMapper.selectOrgMstList(orgSearch);
            result.setList(list);
            result.setPageCnt(list != null && list.size() > 0 ? list.size() : 0);
        }
        return result;
    }
}

