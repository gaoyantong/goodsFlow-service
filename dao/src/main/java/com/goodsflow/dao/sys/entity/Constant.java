package com.goodsflow.dao.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.goodsflow.dao.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_constant")
public class Constant extends BaseEntity {
    private String code;
    private String name;
    private String parent;
    private String vals;
    private String remarks;
}
