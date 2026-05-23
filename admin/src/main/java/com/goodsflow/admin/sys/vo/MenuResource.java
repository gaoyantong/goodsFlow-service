package com.goodsflow.admin.sys.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MenuResource {
    private String id;
    private String name;
    private String icon;
    private String path;
    private List<MenuResource> children = new ArrayList<>();
}
