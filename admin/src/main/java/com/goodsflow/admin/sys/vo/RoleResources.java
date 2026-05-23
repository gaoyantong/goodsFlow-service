package com.goodsflow.admin.sys.vo;

import lombok.Data;

import java.util.List;

@Data
public class RoleResources {
    private String roleId;
    private List<String> resourcesId;
}
