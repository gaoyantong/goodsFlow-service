package com.goodsflow.admin.flow.vo;

import com.goodsflow.dao.PageParams;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FlowTaskSearchParams extends PageParams {
    private String goodsId;
    private String batchNo;
    private String status;
}
