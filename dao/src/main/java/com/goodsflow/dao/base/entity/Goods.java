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
    @NotBlank(message = "请输入货品ID")
    private String goodsId;

    /** 通用名 */
    @NotBlank(message = "请输入通用名")
    private String genericName;

    /** 生产厂商 */
    @NotBlank(message = "请输入生产厂商")
    private String manufacturer;

    /** 规格 */
    @NotBlank(message = "请输入规格")
    private String specification;

    /** 货品单位 */
    @NotBlank(message = "请输入货品单位")
    private String unit;
}
