package com.goodsflow.dao.base.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.goodsflow.dao.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gf_store")
public class Store extends BaseEntity {
    /** 门店ID，业务唯一编码 */
    @NotBlank(message = "storeId is required")
    private String storeId;

    /** 门店名称 */
    @NotBlank(message = "storeName is required")
    private String storeName;
}
