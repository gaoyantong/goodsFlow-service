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
    /** 用户姓名 */
    private String name;

    /** 登录账号 */
    @NotBlank(message = "请输入登录账号")
    private String loginName;

    /** 登录密码 */
    private String password;

    /** 头像地址 */
    private String icon;

    /** 语言偏好 */
    private String language;

    /** 用户描述 */
    private String description;

    /** 指纹标识 */
    private String fingerprint;

    /** 性别 */
    private String sex;

    /** 工号 */
    private String workNum;

    /** 邮箱 */
    private String email;
}
