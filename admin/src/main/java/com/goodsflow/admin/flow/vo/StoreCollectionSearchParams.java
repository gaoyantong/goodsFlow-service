package com.goodsflow.admin.flow.vo;

import com.goodsflow.dao.PageParams;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class StoreCollectionSearchParams extends PageParams {
    private String collectionId;
    private String collectionName;
    private String storeId;
    private String storeName;
}
