package com.albee.albeepoint.api.common.service;
 
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albee.albeepoint.api.common.constant.ErrorCode;
import com.albee.albeepoint.api.common.constant.EnumCommon.EncSts;
import com.albee.albeepoint.api.common.constant.EnumCommon.EncTarget;
import com.albee.albeepoint.api.common.dto.EncInfoDto;
import com.albee.albeepoint.api.common.dto.EncInfoSearchDto;
import com.albee.albeepoint.api.common.dto.ResultListDto;
import com.albee.albeepoint.api.org.dto.OrgSearchDto;
import com.albee.albeepoint.api.org.service.OrgMstService;
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.api.util.EncUtil;
import com.albee.albeepoint.api.util.VdUtil;
import com.albee.albeepoint.mapper.base.t_enc_info.TEncInfo;
import com.albee.albeepoint.mapper.base.t_enc_info.TEncInfoMapper;
import com.albee.albeepoint.mapper.base.t_org_mst.TOrgMst;
import com.albee.albeepoint.mapper.common.EncInfoMapper;

import java.util.List;


/**
 * EncInfoService
 * 암호화 정보 관리 서비스
 * @author : Hwang 
 * @since : 2026-04-01
 */
@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class EncInfoService {
    @Autowired
    private TEncInfoMapper tEncInfoMapper;

    @Autowired
    private EncInfoMapper encInfoMapper;

    @Autowired
    private OrgMstService orgMstService;

    /* 암호화 정보 등록 */
    public void regEncInfo(EncInfoDto dom) {
        TEncInfo entity = dom.getEntity();
        VdUtil.ec(this.tEncInfoMapper.insert(entity) <= 0, 
            EncTarget.ORG.equalsCode(dom.getTargetGbCd()) ? ErrorCode.BIZ_ERR_001137 : ErrorCode.BIZ_ERR_001138);
    }

    public TEncInfo modEncInfo(EncInfoDto dom) {
        // TargetVal : 기관번호(orgNo)
        TOrgMst org = orgMstService.getOrgMstWec(new OrgSearchDto(Long.valueOf(dom.getTargetVal())));

        TEncInfo entity = getEncInfo(dom.getTargetGbCd(), dom.getTargetVal());
        ComUtil.objectCopy(entity, dom);

        /// 암호화키 생성
        String encKey = EncUtil.getSalt();
        String salt = EncUtil.getSalt();
        dom.setEncKey(encKey);
        dom.setSaltVal(salt);
        dom.setEncStsCd(EncSts.NORMAL.getCode());

        TEncInfo encInfo = dom.getEntity();
        this.tEncInfoMapper.updateByPrimaryKeySelective(encInfo);
        return this.tEncInfoMapper.selectByPrimaryKey(encInfo.getTargetGbCd(), encInfo.getTargetVal());
    }

    public void delEncInfo(EncInfoDto dom) {
        this.tEncInfoMapper.deleteByPrimaryKey(dom.getTargetGbCd(), dom.getTargetVal());
    }

    public TEncInfo getEncInfo(String targetGbCd, String targetVal) {
        return this.tEncInfoMapper.selectByPrimaryKey(targetGbCd, targetVal);
    }

    public ResultListDto<EncInfoDto> getEncInfoList(EncInfoSearchDto dom) {
        ResultListDto<EncInfoDto> result = new ResultListDto<>();
        Long totalCnt = this.encInfoMapper.selectEncInfoListTotalCnt(dom);
        result.setTotalCnt(totalCnt != null ? totalCnt : 0L);
        if(result.getTotalCnt() > 0){
            List<EncInfoDto> list = this.encInfoMapper.selectEncInfoList(dom);
            result.setList(list);
            result.setPageCnt(list != null && list.size() > 0 ? list.size() : 0);
        }
        return result;
    }
}
