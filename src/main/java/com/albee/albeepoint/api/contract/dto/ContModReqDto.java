package com.albee.albeepoint.api.contract.dto;

 
import com.albee.albeepoint.api.common.constant.EnumDescConst;
import com.albee.albeepoint.api.contract.constant.EnumCont; 
import com.albee.albeepoint.api.util.DateUtil; 
import io.swagger.v3.oas.annotations.media.Schema; 
import lombok.Data; 
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
 
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data 
@Schema(description = "계약정보 수정 VO")
public class ContModReqDto {
    @Schema(description = "계약번호", requiredMode = REQUIRED, maxLength = 100, example = "20001")
    private Long contNo;

    @Schema(description = "계약명", requiredMode = REQUIRED, maxLength = 100, example = "알비 포인트 서비스")
    private String contNm;

    @Schema(description = EnumDescConst.descPtType, requiredMode = REQUIRED, defaultValue = "MILEAGE", example = "MILEAGE")
    private EnumCont.PtType ptTypeCd;

    @DateTimeFormat(pattern = "yyyyMMdd")
    @Schema(description = "계약 시작 일자", requiredMode = REQUIRED, example = "20230101")
    private String startDay;
    public Timestamp getStartDt(){
        return DateUtil.convStringToTimestampForStart(this.startDay);
    }

    @DateTimeFormat(pattern = "yyyyMMdd")
    @Schema(description = "계약 종료 일자", requiredMode = REQUIRED, example = "20231231")
    private String endDay;
    public Timestamp getEndDt(){
        return DateUtil.convStringToTimestampForEnd(this.endDay);
    }

    @DateTimeFormat(pattern = "yyyyMMdd")
    @Schema(description = "적립 종료 일자", requiredMode = REQUIRED, example = "20230101")
    private String isuStartDay;
    public Timestamp getIsuStartDt(){
        return DateUtil.convStringToTimestampForStart(this.isuStartDay);
    }

    @DateTimeFormat(pattern = "yyyyMMdd")
    @Schema(description = "적립 종료 일자", requiredMode = REQUIRED, example = "20231231")
    private String isuEndDay;
    public Timestamp getIsuEndDt(){
        return DateUtil.convStringToTimestampForEnd(this.isuEndDay);
    }

    @DateTimeFormat(pattern = "yyyyMMdd")
    @Schema(description = "사용 종료 일자", requiredMode = REQUIRED, example = "20230101")
    private String useStartDay;
    public Timestamp getUseStartDt(){
        return DateUtil.convStringToTimestampForStart(this.useStartDay);
    }

    @DateTimeFormat(pattern = "yyyyMMdd")
    @Schema(description = "사용 종료 일자", requiredMode = REQUIRED, example = "20231231")
    private String useEndDay;
    public Timestamp getUseEndDt(){
        return DateUtil.convStringToTimestampForEnd(this.useEndDay);
    }


    @Schema(description = EnumDescConst.descUseStartDtCalcType, requiredMode = REQUIRED, defaultValue = "NONE", example = "NONE")
    private EnumCont.UseStartDtCalcType useStartDtCalcTypeCd;

    @Schema(description = "사용 시작 일자 계산 값", requiredMode = NOT_REQUIRED, example = "7")
    private Integer useStartDtCalcVal;

    @Schema(description = EnumDescConst.descUsePeriodCalcType, requiredMode = REQUIRED, defaultValue = "NONE", example = "NONE")
    private EnumCont.UsePeriodCalcType usePeriodCalcTypeCd;

    @Schema(description = "사용 기간 계산 값", requiredMode = NOT_REQUIRED, example = "7")
    private Integer usePeriodCalcVal;

    @Schema(description = EnumDescConst.descContSts, requiredMode = REQUIRED, defaultValue = "NORMAL", example = "NORMAL")
    private EnumCont.ContSts contStsCd;

    @Schema(description = "혼합 사용 여부", requiredMode = NOT_REQUIRED, defaultValue = "N", example = "Y")
    private String mixUseYn;


    @Schema(description = EnumDescConst.descIsuPeriodLimitType, requiredMode = REQUIRED, defaultValue = "NONE", example = "NONE")
    private EnumCont.IsuPeriodLimitType isuPeriodLimitTypeCd;

    @Schema(description = "기간 제한 최대 적립 포인트", requiredMode = NOT_REQUIRED, example = "999999999")
    private Long isuPeriodMaxPt;

    @Schema(description = "기간 제한 최대 적립 건수", requiredMode = NOT_REQUIRED, example = "10")
    private Integer isuPeriodMaxCnt;

    @Schema(description = EnumDescConst.descOnceIsuLimitType, requiredMode = REQUIRED, defaultValue = "NONE", example = "NONE")
    private EnumCont.OnceIsuLimitType onceIsuLimitTypeCd;

    @Schema(description = "1회당 고정 적립 포인트", requiredMode = NOT_REQUIRED, example = "1000")
    private Long onceIsuFixPt;

    @Schema(description = "1회당 적립 비율(백분율)", requiredMode = NOT_REQUIRED, example = "0.1")
    private Double onceIsuPurchaseRate;

    @Schema(description = "1회당 최소 적립 포인트", requiredMode = NOT_REQUIRED, example = "100")
    private Long onceIsuMinPt;

    @Schema(description = "1회당 최대 적립 포인트", requiredMode = NOT_REQUIRED, example = "10000")
    private Long onceIsuMaxPt;

    @Schema(description = EnumDescConst.descUsePeriodLimitType, requiredMode = NOT_REQUIRED, defaultValue = "NONE", example = "NONE")
    private EnumCont.UsePeriodLimitType usePeriodLimitTypeCd;

    @Schema(description = "기간내 최대 사용 포인트 제한", requiredMode = NOT_REQUIRED, example = "10000")
    private Long usePeriodMaxPt;

    @Schema(description = "기간내 최대 사용 건수 제한", requiredMode = NOT_REQUIRED, example = "10")
    private Integer usePeriodMaxCnt;

    @Schema(description = EnumDescConst.descOnceUseLimitType, requiredMode = NOT_REQUIRED, defaultValue = "NONE", example = "NONE")
    private EnumCont.OnceUseLimitType onceUseLimitTypeCd;

    @Schema(description = "1회당 고정 사용 포인트", requiredMode = NOT_REQUIRED, example = "1000")
    private Long onceUseFixPt;

    @Schema(description = "1회당 사용 비율(백분율)", requiredMode = NOT_REQUIRED, example = "0.1")
    private Double onceUsePurchaseRate;

    @Schema(description = "1회당 최소 사용 포인트", requiredMode = NOT_REQUIRED, example = "100")
    private Long onceUseMinPt;

    @Schema(description = "1회당 최대 사용 포인트", requiredMode = NOT_REQUIRED, example = "10000")
    private Long onceUseMaxPt;

    @Schema(description = EnumDescConst.descTotalIsuLimitType, requiredMode = NOT_REQUIRED, example = "NONE")
    private EnumCont.TotalIsuLimitType totalIsuLimitTypeCd;

    @Schema(description = "총 적립 가능 포인트 제한", requiredMode = NOT_REQUIRED, example = "1000000")
    private Long totalIsuAblePt;

    @Schema(description = "총 적립 가능 건수 제한", requiredMode = NOT_REQUIRED, example = "10000")
    private Long totalIsuAbleCnt;

    @Schema(description = "포인트 사용시 최소 구매 금액", requiredMode = NOT_REQUIRED, example = "50000")
    private Long minPurchaseAmt;

    // @JsonIgnore
    // public ContMstEntity getEntity(){
    //     return (ContMstEntity)ComUtil.objectCopy(this, ContMstEntity.class);
    // }

    // @JsonIgnore
    // public ContSearch getContSearch(){
    //     return (ContSearch)ComUtil.objectCopy(this, ContSearch.class);
    // }


    // @JsonIgnore
    // public ContModReqIVo getIVo(){
    //     return (ContModReqIVo) ComUtil.votoivoCopy(this);
    // }
}