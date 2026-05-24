package com.goodsflow.dao.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.goodsflow.dao.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class Role extends BaseEntity {
    /** 角色名称 */
    @NotBlank(message = "请输入角色名称")
    private String name;

    /** 角色编码 */
    @NotBlank(message = "请输入角色编码")
    private String roleCode;
}
