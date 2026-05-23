package com.goodsflow.dao.base.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.goodsflow.dao.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("gf_goods")
public class Goods extends BaseEntity {
    /** 货品ID，业务唯一编码 */
    @NotBlank(message = "goodsId is required")
    private String goodsId;

    /** 通用名 */
    @NotBlank(message = "genericName is required")
    private String genericName;

    /** 生产厂商 */
    @NotBlank(message = "manufacturer is required")
    private String manufacturer;

    /** 规格 */
    @NotBlank(message = "specification is required")
    private String specification;

    /** 货品单位 */
    @NotBlank(message = "unit is required")
    private String unit;
}
