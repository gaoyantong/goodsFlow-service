package com.goodsflow.admin.sys.vo;

import com.goodsflow.dao.sys.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserWithRole extends User {
    private String role;
    private String roleName;
}
