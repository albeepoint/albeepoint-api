package com.albee.albeepoint.api.contract.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ContBrchDto implements Serializable {
    private Long contNo;

    private Long orgNo;

    private Long brchNo;

    private Long contSeq;

    private String saveCanYn;

    private String useCanYn;

    private String contBrchStsCd;

    private Long totalIsuAblePt;

    private Long totalIsuAbleCnt;

    private String regUserId;

    private LocalDateTime regDt;

    private String lastModUserId;

    private LocalDateTime lastModDt;

    private static final long serialVersionUID = 1L;
 
}