package com.goodsflow.admin.flow.controller;

import java.time.LocalDate;
import java.time.YearMonth;

final class FlowMonthUtils {
    private FlowMonthUtils() {
    }

    static YearMonth parse(String value) {
        if (value == null || value.length() < 7) {
            return null;
        }
        return YearMonth.parse(value.substring(0, 7));
    }
}
