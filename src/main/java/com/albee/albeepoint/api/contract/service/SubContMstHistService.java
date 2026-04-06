package com.albeepoint.core.biz.cont.service;

import com.albeepoint.core.biz.cont.models.dto.ContSearch;
import com.albeepoint.core.biz.cont.models.entity.SubContMstEntity;
import com.albeepoint.core.biz.cont.models.entity.SubContMstHistEntity;
import com.albeepoint.core.biz.cont.repository.SubContMstHistMapper;
import com.albeepoint.core.common.models.dto.ResultListDto;
import com.albeepoint.core.common.util.CommUtil;
import com.albeepoint.core.common.util.VdUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.albeepoint.core.common.models.ErrorCode.BIZ_ERR_001087;


@Service
@Transactional
public class SubContMstHistService {
    @Autowired
    SubContMstHistMapper subContMstHistMapper;

    public static final Logger log = LogManager.getLogger(SubContMstHistService.class);

    public SubContMstHistEntity regSubContMstHist(SubContMstEntity dom) {
        ContSearch contSearch = new ContSearch(dom.getContNo());

        SubContMstHistEntity subContMstHistEntity = (SubContMstHistEntity)CommUtil.objectCopy(dom, SubContMstHistEntity.class);
        subContMstHistEntity.setSubContHistSeq(subContMstHistMapper.selectNextSubContHistSeq(contSearch));

        VdUtil.ec(subContMstHistMapper.insertSubContMstHist(subContMstHistEntity) <= 0
                , BIZ_ERR_001087, "서브계약이트 인서트 트오류");

        return subContMstHistMapper.selectSubContMstHist(contSearch);
    }

    public SubContMstHistEntity getSubContMstHist(ContSearch dom) {
        return subContMstHistMapper.selectSubContMstHist(dom);
    }

    public ResultListDto<SubContMstHistEntity> getSubContMstHistList(ContSearch dom) {
        ResultListDto<SubContMstHistEntity> result = new ResultListDto<>();
        Long totalCnt = subContMstHistMapper.selectSubContMstHistListTotalCnt(dom);
        result.setTotalCnt(totalCnt != null ? totalCnt : 0L);
        if(result.getTotalCnt() > 0){
            List<SubContMstHistEntity> list = subContMstHistMapper.selectSubContMstHistList(dom);
            result.setList(list);
            result.setPageCnt(list != null && list.size() > 0 ? list.size() : 0);
        }
        return result;
    }


}
