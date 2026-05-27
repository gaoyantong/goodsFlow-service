package com.goodsflow.dao.flow.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.goodsflow.dao.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gf_store_collection")
public class StoreCollection extends BaseEntity {
    /** 门店集合ID，系统生成 */
    private String collectionId;

    /** 集合名称 */
    @NotBlank(message = "请输入集合名称")
    private String collectionName;

    /** 集合门店明细，仅接口传输使用 */
    @TableField(exist = false)
    private List<StoreCollectionStore> stores;
}
