package com.goodsflow.admin.flow.vo;

import com.goodsflow.dao.PageParams;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RetailSearchParams extends PageParams {
    private String taskId;
    private String storeId;
    private String storeName;
    private String goodsId;
    private String batchNo;
}
