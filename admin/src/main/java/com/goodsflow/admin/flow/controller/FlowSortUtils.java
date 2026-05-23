package com.goodsflow.admin.flow.controller;

import com.goodsflow.admin.flow.vo.BusinessDateSortParams;
import org.springframework.util.StringUtils;

final class FlowSortUtils {
    private FlowSortUtils() {
    }

    static boolean isBusinessDateAsc(BusinessDateSortParams params) {
        return StringUtils.hasText(params.getBusinessDateSort()) && "ascend".equals(params.getBusinessDateSort());
    }
}
