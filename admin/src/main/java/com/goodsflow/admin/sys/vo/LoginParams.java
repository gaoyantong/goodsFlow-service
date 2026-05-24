package com.goodsflow.admin.sys.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginParams {
    @NotBlank(message = "请输入登录账号")
    private String loginName;

    @NotBlank(message = "请输入密码")
    private String password;
}
