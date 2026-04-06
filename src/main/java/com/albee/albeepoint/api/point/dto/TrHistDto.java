package com.albee.albeepoint.api.point.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TrHistDto implements Serializable {
    private Long trSno;

    private Long memberNo;

    private LocalDateTime trDt;

    private String trGbCd;

    private String trMethodGbCd;

    private String trDy;

    private Long orgNo;

    private Long brchNo;

    private Long trPt;

    private Long befPt;

    private Long afrPt;

    private Long purchaseAmt;

    private Long orglTrSno;

    private String regUserId;

    private LocalDateTime regDt;

    private String lastModUserId;

    private LocalDateTime lastModDt;

    private static final long serialVersionUID = 1L;

}