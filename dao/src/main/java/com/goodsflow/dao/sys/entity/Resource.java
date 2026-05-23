package com.goodsflow.dao.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.goodsflow.dao.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_resource")
public class Resource extends BaseEntity {
    /** 资源名称 */
    private String name;

    /** 菜单图标 */
    private String icon;

    /** 路由路径 */
    private String path;

    /** 资源描述 */
    private String description;

    /** 父级资源ID */
    private String parentId;

    /** 资源类型，MENU=菜单 */
    private String type;

    /** 中文名称 */
    private String nameCh;
}
