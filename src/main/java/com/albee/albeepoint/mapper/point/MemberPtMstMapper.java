package com.albee.albeepoint.mapper.point;
 
import org.springframework.stereotype.Component;

import com.albee.albeepoint.api.point.dto.CalcBefAfrBalDto;
import com.albee.albeepoint.api.point.dto.MemberPtDto;
import com.albee.albeepoint.api.point.dto.MemberPtSearchDto;
import com.albee.albeepoint.api.point.dto.PtSearchDto;

import java.util.List;

@Component
public interface MemberPtMstMapper {


    MemberPtDto selectMemberPtMst(MemberPtSearchDto dom);


    CalcBefAfrBalDto selectMemberPtMstCalBefAfrBalPt(CalcBefAfrBalDto dom);
    
    int updateUseCancelMemberPtMst(MemberPtDto dom);

    int updateIsuMemberPtMst(MemberPtDto dom);

    int updateIsuCancelMemberPtMst(MemberPtDto dom);


    Long selectMemberPtMstListTotalCnt(PtSearchDto dom);

    List<MemberPtDto> selectMemberPtMstList(PtSearchDto dom);

}
