package com.goodsflow.admin.sys.vo;

import com.goodsflow.dao.PageParams;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoleSearchParams extends PageParams {
    private String name;
}
