package com.albeepoint.core.common.filter.models.dto;

import com.albeepoint.core.common.filter.models.EnumFilter;
import com.albeepoint.core.common.filter.models.entity.UriMstEntity;
import com.albeepoint.core.common.models.dto.BaseDto;
import com.albeepoint.core.common.util.CommUtil;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UriMstDto extends BaseDto {
    private Long rowNum;
    private String uri;    // URI
    private EnumFilter.UriTrGb trGbCd;    // 거래구분코드
    private EnumFilter.AcsPolicy acsPolicyCd;    // 접근정책코드(ALL:전체접근, WHITE:등록IP)
    private String useYn;    // 사용여부
    private String uriInfo;

    public UriMstEntity getEntity(){
        UriMstEntity entity = new UriMstEntity();
        CommUtil.objectCopy(this, entity);
        return entity;
    }
}
