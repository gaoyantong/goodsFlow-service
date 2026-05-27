package com.goodsflow.admin.flow.vo;

import com.goodsflow.dao.flow.entity.FlowTask;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class FlowTaskModifyParams extends FlowTask {
    private List<String> storeIds;

    /** 门店集合ID，选择门店集合生成时使用 */
    private String storeCollectionId;

    /** 门店集合ID列表，多选门店集合生成时使用 */
    private List<String> storeCollectionIds;
}
