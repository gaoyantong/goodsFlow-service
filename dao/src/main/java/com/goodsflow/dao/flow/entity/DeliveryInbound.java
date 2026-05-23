package com.goodsflow.dao.flow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.goodsflow.dao.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gf_delivery_inbound")
public class DeliveryInbound extends BaseEntity {
    /** 数据录入任务ID */
    private String taskId;

    /** 业务日期 */
    private LocalDate businessDate;

    /** 门店ID */
    private String storeId;

    /** 门店名称快照 */
    private String storeName;

    /** 货品ID */
    private String goodsId;

    /** 通用名快照 */
    private String genericName;

    /** 规格快照 */
    private String specification;

    /** 生产厂商快照 */
    private String manufacturer;

    /** 货品单位快照 */
    private String unit;

    /** 批号 */
    private String batchNo;

    /** 有效期 */
    private LocalDate expiryDate;

    /** 入库数量 */
    private Integer inboundQty;
}
