package com.goodsflow.dao.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.goodsflow.dao.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role_resource")
public class RoleResource extends BaseEntity {
    /** 角色ID */
    private String roleId;

    /** 资源ID */
    private String resourceId;
}
