package com.goodsflow.admin.base.vo;

import com.goodsflow.dao.PageParams;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GoodsSearchParams extends PageParams {
    private String goodsId;
    private String genericName;
}
