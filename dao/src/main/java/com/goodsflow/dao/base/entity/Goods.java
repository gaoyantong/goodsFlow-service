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
    @NotBlank(message = "goodsId is required")
    private String goodsId;

    @NotBlank(message = "genericName is required")
    private String genericName;

    @NotBlank(message = "manufacturer is required")
    private String manufacturer;

    @NotBlank(message = "specification is required")
    private String specification;

    @NotBlank(message = "unit is required")
    private String unit;
}
