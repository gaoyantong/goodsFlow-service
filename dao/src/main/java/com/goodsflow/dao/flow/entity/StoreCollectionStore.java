package com.goodsflow.dao.flow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.goodsflow.dao.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gf_store_collection_store")
public class StoreCollectionStore extends BaseEntity {
    /** 门店集合主表ID */
    private String collectionDbId;

    /** 门店集合ID，系统生成 */
    private String collectionId;

    /** 门店ID */
    private String storeId;

    /** 门店名称 */
    private String storeName;
}
