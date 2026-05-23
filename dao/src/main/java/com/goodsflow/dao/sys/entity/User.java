package com.goodsflow.dao.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.goodsflow.dao.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {
    private String name;

    @NotBlank(message = "loginName is required")
    private String loginName;

    private String password;
    private String icon;
    private String language;
    private String description;
    private String fingerprint;
    private String sex;
    private String workNum;
    private String email;
}
