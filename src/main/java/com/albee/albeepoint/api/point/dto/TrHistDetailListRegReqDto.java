package com.albee.albeepoint.api.point.dto;
 
import com.albee.albeepoint.api.point.constant.EnumPoint; 
import io.swagger.v3.oas.annotations.media.Schema; 
import lombok.Data; 
 

import java.util.List;


@Data 
@Schema(description = "회원 계약 포인트 거래내역 상세 멀티 등록 요청 DTO")
public class TrHistDetailListRegReqDto {

    private Long memberNo; 

    private Long trSno;    // 거래일련번호

    private EnumPoint.TrGb trGbCd;

    private Long contNo;    // 계약번호

    private Long orgNo;    // 기관번호

    private Long brchNo;    // 지점번호

    private Long orglTrSno;

    private Long ptNo;

    private Long memberPtNo;

    private String trMethodGbCd;

    private String regUserId; 

    private String lastModUserId; 

    private List<String> trGbCdList;    // 거래구분코드 목록

    private List<PtReqDto> isuCancelTargetList;

    private List<PtReqDto> useTargetList;

    private List<PtReqDto> useCancelTargetList;
}
