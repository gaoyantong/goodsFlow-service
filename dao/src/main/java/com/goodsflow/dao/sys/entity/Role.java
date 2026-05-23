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
    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "roleCode is required")
    private String roleCode;
}
