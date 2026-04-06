package com.albee.albeepoint.api.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EncDto {
    private String orgCd;
    private String encStr;
    private String decStr;
}
