package com.goodsflow.admin.sys.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ResourceTreeItem {
    private String id;
    private String key;
    private String title;
    private List<ResourceTreeItem> children = new ArrayList<>();
}
