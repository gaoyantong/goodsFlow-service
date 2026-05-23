package com.goodsflow.admin.sys.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginParams {
    @NotBlank(message = "loginName is required")
    private String loginName;

    @NotBlank(message = "password is required")
    private String password;
}
