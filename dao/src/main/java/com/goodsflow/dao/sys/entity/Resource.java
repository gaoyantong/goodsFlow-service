package com.goodsflow.dao.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.goodsflow.dao.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_resource")
public class Resource extends BaseEntity {
    private String name;
    private String icon;
    private String path;
    private String description;
    private String parentId;
    private String type;
    private String nameCh;
}
