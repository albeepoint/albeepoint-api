package com.albee.albeepoint.api.common.dto;


import com.albee.albeepoint.api.common.constant.AlbeeConst;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
@NoArgsConstructor
public class SearchDto {

    private Long memberNo;    // 회원번호

    private Long orgNo;    // 기관번호

    private String orgCd;    //

    private Long brchNo;    // 지점번호

    private String brchCd;    //

    private String memberId;    //

    private String memberIdEnc;    //

    private String memberNm;

    private Integer requestPage;  // 1부터 시작

    private Integer requestCnt;

    private Timestamp startDt;

    private Timestamp endDt;

    private String baseDt;

    private Long baseBalPt;

    private Integer startIndex;

    private Integer endIndex;

    @JsonIgnore
    private String regUserId = AlbeeConst.apiUserId;            // 등록자ID

    @JsonIgnore
    private String lastModUserId = AlbeeConst.apiUserId;        // 최종수정자ID

    public Integer getRequestPage() {
        return requestPage;
    }

    public SearchDto(Long memberNo, Long orgNo) {
        this.memberNo = memberNo;
        this.orgNo = orgNo;
    }

    public void setRequestPage(Integer requestPage) {
        Integer si = null;
        if(requestPage != null && requestPage > 0 && requestCnt != null && requestCnt > 0){
            si = (requestPage - 1) * requestCnt + 1;
        }
        this.startIndex = si;

        Integer ei = null;
        if(requestPage != null && requestPage > 0 && requestCnt != null && requestCnt > 0){
            ei = (requestPage - 1) * requestCnt + requestCnt;
        }
        this.endIndex = ei;
        this.requestPage = requestPage;
    }

    public Integer getRequestCnt() {
        return requestCnt;
    }

    public void setRequestCnt(Integer requestCnt) {
        Integer si = null;
        if(requestPage != null && requestPage > 0 && requestCnt != null && requestCnt > 0){
            si = (requestPage - 1) * requestCnt + 1;
        }
        this.startIndex = si;

        Integer ei = null;
        if(requestPage != null && requestPage > 0 && requestCnt != null && requestCnt > 0){
            ei = (requestPage - 1) * requestCnt + requestCnt;
        }
        this.endIndex = ei;
        this.requestCnt = requestCnt;
    }


}
