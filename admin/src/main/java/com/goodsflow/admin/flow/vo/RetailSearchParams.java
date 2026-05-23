package com.goodsflow.admin.flow.vo;

import com.goodsflow.dao.PageParams;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class RetailSearchParams extends PageParams implements BusinessDateSortParams {
    private String taskId;
    private String taskNo;
    private String storeId;
    private String storeName;
    private String goodsId;
    private String batchNo;
    private String businessDateSort;
    private LocalDate businessDateStart;
    private LocalDate businessDateEnd;
    private List<String> ids;
    private String exportMonth;
    private Boolean excludeBatchNo;
}
