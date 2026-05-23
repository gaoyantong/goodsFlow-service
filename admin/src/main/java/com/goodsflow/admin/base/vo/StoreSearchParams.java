package com.goodsflow.admin.base.vo;

import com.goodsflow.dao.PageParams;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StoreSearchParams extends PageParams {
    private String storeId;
    private String storeName;
}
