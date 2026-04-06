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
import com.albee.albeepoint.api.contract.dto.ContRelListRegReqDto;
import com.albee.albeepoint.api.contract.dto.ContRelRegReqDto;
import com.albee.albeepoint.api.util.VdUtil;
import com.albee.albeepoint.mapper.base.t_cont_rel.TContRelMapper;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional(rollbackFor = {InterruptedException.class,  RuntimeException.class, Exception.class})
public class ContRelService {
    @Autowired
    TContRelMapper tContRelMapper;
    @Autowired
    ContRelMapper contRelMapper;

    public void regContRel(ContRelListRegReqDto reqDto) {
        Long contRelNo = null;
        Long contRelSeq = null;
        for(ContRelRegReqDto contRel : reqDto.getContRelList()) {
            if(VdUtil.isNotEmpty(contRelNo) && VdUtil.isNotEmpty(contRel.getContRelNo())){
                VdUtil.notEqualEc(contRelNo, contRel.getContRelNo(), ErrorCode.BIZ_ERR_001131);
            }
            ContRelSearch contRelSearch = new ContRelSearch(contRel.getContRelNo(), contRel.getContRelGbCd(), contRel.getContRole(), "Y");
            contRelSearch.setContNo(contRel.getContNo());
            ContRelEntity contRelEty = getContRel(contRelSearch);

            if(VdUtil.isEmpty(contRelEty)){
                contRelNo = VdUtil.isEmpty(contRelNo) ? contRelMapper.selectNewContRelNo() : contRelNo;
                contRelSeq = contRelMapper.selectNewContRelSeq(new ContRelSearch(contRelNo, "Y"));
                contRelEty = contRel.getEntity();
                contRelEty.setContRelNo(contRelNo);
                contRelEty.setContRelSeq(contRelSeq);
                VdUtil.ec(this.tContRelMapper.insert(contRelEty) < 1, ErrorCode.BIZ_ERR_001131, "계약연계 등록 오류");
            }else{
                contRelNo = contRel.getContRelNo();
                contRelEty.setContRelInfo(contRel.getContRelInfo());
                contRelEty.setContRelUseYn(contRel.getContRelUseYn());
                // 동일계약에 대한 내용이지만, 연계계약번호가 상이 하면 신규로 등록
                if(VdUtil.isNotEqual(contRelEty.getLinkContNo(), VdUtil.isNotEmpty(contRel.getLinkContNo()))){
                    contRelSeq = contRelMapper.selectNewContRelSeq(new ContRelSearch(contRel.getContRelNo(), "Y"));
                    contRelEty.setContRelSeq(contRelSeq);
                    VdUtil.ec(this.tContRelMapper.insert(contRelEty) < 1, ErrorCode.BIZ_ERR_001131, "계약연계 등록 오류"); 
                }else {
                    VdUtil.ec(this.tContRelMapper.updateByPrimaryKeySelective(contRelEty) < 1, ErrorCode.BIZ_ERR_001131, "계약연계 수정 오류");                    
                }
            }
        } 
    }

    public ContRelEntity modContRel(ContRelRegReqIVo dom) {
        ContRelEntity contRelEty = dom.getEntity();
        contRelMapper.updateContRel(contRelEty);
        return contRelMapper.selectContRel(dom.getContRelSearch());
    }

    public ContRelEntity getContRel(ContRelSearch dom) {
        return contRelMapper.selectContRel(dom);
    }

    public ContRelEntity getContRelWec(ContRelSearch dom) {
        ContRelEntity contRelEty = contRelMapper.selectContRel(dom);
        VdUtil.emptyEc(contRelEty, BIZ_ERR_001129);
        return contRelEty;
    }

    public ResultListDto<ContRelEntity> getContRelList(ContRelSearch dom) {
        ResultListDto<ContRelEntity> result = new ResultListDto<>();
        Long totalCnt = contRelMapper.selectContRelListTotalCnt(dom);
        result.setTotalCnt(totalCnt != null ? totalCnt : 0L);
        if(result.getTotalCnt() > 0){
            List<ContRelEntity> list = contRelMapper.selectContRelList(dom);
            result.setList(list);
            result.setPageCnt(list != null && list.size() > 0 ? list.size() : 0);
        }
        return result;
    }

}
