package com.albeepoint.core.common.filter.service;

import com.albeepoint.core.common.filter.models.dto.UriMstDto;
import com.albeepoint.core.common.filter.models.dto.UriMstSearch;
import com.albeepoint.core.common.filter.models.entity.UriMstEntity;
import com.albeepoint.core.common.filter.repository.UriMstMapper;
import com.albeepoint.core.common.models.dto.ResultListDto;
import com.albeepoint.core.common.util.VdUtil;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.albeepoint.core.common.models.ErrorCode.*;


@RequiredArgsConstructor
@Service
@Transactional
public class UriMstService {
    @Autowired
    UriMstMapper uriMstMapper;

    public static final Logger log = LogManager.getLogger(UriMstService.class);

    public UriMstEntity regUriMst(UriMstDto dom) {
        VdUtil.ec(VdUtil.isNotEmpty(getUriMst(new UriMstSearch(dom))), BIZ_ERR_001077, "기등록 URL");
        VdUtil.ec(uriMstMapper.insertUriMst(dom.getEntity()) <= 0, BIZ_ERR_001079, "URL 등록 반영 오류");
        return uriMstMapper.selectUriMst(new UriMstSearch(dom));
    }

    public UriMstEntity modUriMst(UriMstDto dom) {
        VdUtil.ec(VdUtil.isNotEmpty(getUriMst(new UriMstSearch(dom))), BIZ_ERR_001078, "등록 URL 없음");
        VdUtil.ec(uriMstMapper.updateUriMst(dom.getEntity()) <= 0, BIZ_ERR_001079, "URL 등록 반영 오류");
        return uriMstMapper.selectUriMst(new UriMstSearch(dom));
    }

    public void delUriMst(UriMstDto dom) {
        uriMstMapper.deleteUriMst(dom.getEntity());
    }

    public UriMstEntity getUriMst(UriMstSearch dom) {
        return uriMstMapper.selectUriMst(dom);
    }

    public ResultListDto<UriMstEntity> getUriMstList(UriMstSearch dom) {
        ResultListDto<UriMstEntity> result = new ResultListDto<>();
        Long totalCnt = uriMstMapper.selectUriMstListTotalCnt(dom);
        result.setTotalCnt(totalCnt != null ? totalCnt : 0L);
        if(result.getTotalCnt() > 0){
            List<UriMstEntity> list = uriMstMapper.selectUriMstList(dom);
            result.setList(list);
            result.setPageCnt(list != null && list.size() > 0 ? list.size() : 0);
        }
        return result;
    }
}
