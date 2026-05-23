package com.goodsflow.admin.flow.vo;

import com.goodsflow.dao.flow.entity.FlowTask;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class FlowTaskModifyParams extends FlowTask {
    private List<String> storeIds;
}
