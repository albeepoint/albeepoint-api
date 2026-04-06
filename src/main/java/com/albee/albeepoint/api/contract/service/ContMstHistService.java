package com.albee.albeepoint.api.contract.service;

import com.albee.albeepoint.api.common.constant.ErrorCode;
import com.albee.albeepoint.api.common.dto.ResultListDto;
import com.albee.albeepoint.api.contract.dto.ContSearchDto;
import com.albee.albeepoint.api.util.ComUtil;
import com.albee.albeepoint.api.util.VdUtil;
import com.albee.albeepoint.mapper.base.t_cont_mst.TContMst;
import com.albee.albeepoint.mapper.base.t_cont_mst_hist.TContMstHist;
import com.albee.albeepoint.mapper.base.t_cont_mst_hist.TContMstHistExample;
import com.albee.albeepoint.mapper.base.t_cont_mst_hist.TContMstHistMapper;
import com.albee.albeepoint.mapper.common.GetIdMapper; 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
 


@Service
@Transactional
public class ContMstHistService {
    @Autowired
    TContMstHistMapper contMstHistMapper;

    @Autowired
    GetIdMapper getIdMapper;


    public static final Logger log = LogManager.getLogger(ContMstHistService.class);

    public TContMstHist regContMstHist(TContMst tContMst) {
        ContSearchDto contSearch = new ContSearchDto(tContMst.getContNo());

        TContMstHist contMstHistentity = (TContMstHist)ComUtil.objectCopy(tContMst, TContMstHist.class);


        Long contMstHistId = getIdMapper.createContMstHistId(tContMst.getContNo()); 
        contMstHistentity.setContHistSeq(contMstHistId);

        VdUtil.ec(contMstHistMapper.insert(contMstHistentity) <= 0
                , ErrorCode.BIZ_ERR_001088, "계약이력 인서트 오류");
        contSearch.setContHistSeq(contMstHistId);
 
        TContMstHist contMstHist = this.contMstHistMapper.selectByPrimaryKey(contMstHistId, contMstHistId);
        return contMstHist;
    }

    public TContMstHist getContMstHist(Long contNo, Long contMstHistId) {
        TContMstHist contMstHist = this.contMstHistMapper.selectByPrimaryKey(contMstHistId, contMstHistId); 
        return contMstHist;
    }

    public TContMstHist getContMstHistWec(Long contNo, Long contMstHistId) {
        TContMstHist contMstHist = this.getContMstHist(contNo, contMstHistId);
        VdUtil.emptyEc(contMstHist, ErrorCode.BIZ_ERR_001104);
        return contMstHist;
    }

    public ResultListDto<TContMstHist> getContMstHistList(Long contNo) {
        ResultListDto<TContMstHist> result = new ResultListDto<>();
        TContMstHistExample example = new TContMstHistExample();
        example.createCriteria().andContNoEqualTo(contNo);
        List<TContMstHist> list = this.contMstHistMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(list)){
            result.setTotalCnt(0L);
            result.setPageCnt(0);
            return result;
        }
        result.setTotalCnt((long)list.size());
        result.setPageCnt(list.size());
        result.setList(list);
        return result;
    }


}
