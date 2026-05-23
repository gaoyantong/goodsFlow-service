package com.goodsflow.dao;

import lombok.Data;

@Data
public class PageParams {
    private long current = 1;
    private long pageSize = 10;
}
