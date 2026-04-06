package com.albee.albeepoint.api.common.service;
  
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.albee.albeepoint.api.common.constant.ErrorCode;
import com.albee.albeepoint.api.common.dto.IpFilterDto;
import com.albee.albeepoint.api.common.dto.IpFilterSearchDto;
import com.albee.albeepoint.api.common.dto.ResultListDto;
import com.albee.albeepoint.api.contract.dto.ContSearchDto;
import com.albee.albeepoint.api.contract.service.ContMstService;
import com.albee.albeepoint.api.util.VdUtil;
import com.albee.albeepoint.mapper.base.t_cont_mst.TContMst;
import com.albee.albeepoint.mapper.base.t_cont_mst.TContMstMapper;
import com.albee.albeepoint.mapper.base.t_ip_filter.TIpFilter;
import com.albee.albeepoint.mapper.base.t_ip_filter.TIpFilterMapper;

import java.util.List;
 
@RequiredArgsConstructor
@Service
@Transactional
public class IpFilterService {
    @Autowired
    TIpFilterMapper ipFilterMapper;
    @Autowired
    TContMstMapper contMstMapper;
    @Autowired
    ContMstService contMstService;

    public static final Logger log = LogManager.getLogger(IpFilterService.class);

    public TIpFilter regIpFilter(IpFilterDto dom) {
        VdUtil.emptyEc(dom.getIpAddr(), ErrorCode.BIZ_ERR_001082, "IP주소 미입력");
        VdUtil.emptyEc(dom.getBizRoleCd(), ErrorCode.BIZ_ERR_001081, "업무역할 미입력");
        if(dom.getContNo() == null) {
            dom.setContNo(0L);  // contNo 가 null 입력이면 0 으로 설정
        }

        // contNo 가 null 도 아니고 0 도 아닌 정상적인 값이 입력되었다면 존재하는 계약인지 체크
        if(VdUtil.isNotEmpty(dom.getContNo())){
            TContMst tContMst = this.contMstMapper.selectByPrimaryKey(dom.getContNo());
            VdUtil.emptyEc(tContMst, ErrorCode.BIZ_ERR_001009, "계약정보 없음"); 
        }

        VdUtil.ec(VdUtil.isNotEmpty(getIpFilter(new IpFilterSearchDto(dom))), ErrorCode.BIZ_ERR_001077, "기등록 URL");
        this.ipFilterMapper.insert(dom.getEntity());
        // IpFilterEntity IpFilterEntity = ipFilterMapper.selectIpFilter(new IpFilterSearch(dom.getIpAddr(), dom.getContNo(), dom.getBizRoleCd()));
        IpFilterSearchDto ipFilterSearch = new IpFilterSearchDto(dom.getIpAddr(), dom.getContNo(), dom.getBizRoleCd());
        return getIpFilter(ipFilterSearch);
    }

    public TIpFilter modIpFilter(IpFilterDto dom) {
        VdUtil.emptyEc(dom.getIpAddr(), ErrorCode.BIZ_ERR_001082, "IP주소 미입력");
        VdUtil.emptyEc(dom.getBizRoleCd(), ErrorCode.BIZ_ERR_001081, "업무역할 미입력");
        if(dom.getContNo() == null) {
            dom.setContNo(0L);  // contNo 가 null 입력이면 0 으로 설정
        }

        // contNo 가 null 도 아니고 0 도 아닌 정상적인 값이 입력되었다면 존재하는 계약인지 체크
        if(VdUtil.isNotEmpty(dom.getContNo())){
            TContMst tContMst = this.contMstMapper.selectByPrimaryKey(dom.getContNo());
            VdUtil.emptyEc(tContMst, ErrorCode.BIZ_ERR_001009, "계약정보 없음"); 
        }

        // 수정이므로 기존 등록된 내역이 있어야 함
        TIpFilter ipFilterEntity = ipFilterMapper.selectIpFilter(new IpFilterSearch(dom.getIpAddr(), dom.getContNo(), dom.getBizRoleCd()));
        VdUtil.emptyEc(ipFilterEntity, BIZ_ERR_001059, "IP주소 미등록");

        VdUtil.ec(ipFilterMapper.updateIpFilter(dom.getEntity()) <= 0
                , BIZ_ERR_001060, "IP주소(IP_FILTER) 반영 오류");
        return ipFilterMapper.selectIpFilter(new IpFilterSearch(dom.getIpAddr(), dom.getContNo(), dom.getBizRoleCd()));
    }

    public TIpFilter getIpFilter(IpFilterSearchDto dom) {
        TIpFilter ipFilterEntity = ipFilterMapper.selectIpFilter(dom);
        return ipFilterEntity;
    }

    public ResultListDto<TIpFilter> getIpFilterList(IpFilterSearchDto dom) {
        ResultListDto<TIpFilter> result = new ResultListDto<>();
        Long totalCnt = ipFilterMapper.selectIpFilterListTotalCnt(dom);
        result.setTotalCnt(totalCnt != null ? totalCnt : 0L);
        if(result.getTotalCnt() > 0){
            List<TIpFilter> list = ipFilterMapper.selectIpFilterList(dom);
            result.setList(list);
            result.setPageCnt(list != null && list.size() > 0 ? list.size() : 0);
        }
        return result;
    }
}
