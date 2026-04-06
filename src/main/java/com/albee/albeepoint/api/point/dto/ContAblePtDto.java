package com.albee.albeepoint.api.point.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;


@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ContAblePtDto {

    private Long contNo;

    private Long orgNo;

    private Long brchNo;

    private Long ablePt;

    private List<AblePtDetailDto> ablePtList;

}
