package com.goodsflow.admin.flow.controller;

import java.time.YearMonth;

final class FlowMonthUtils {
    private FlowMonthUtils() {
    }

    static YearMonth parse(String value) {
        if (value == null || value.length() < 6) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.contains("\u5e74") && normalized.contains("\u6708")) {
            normalized = normalized
                .replace("\u5e74", "-")
                .replace("\u6708", "");
        }
        String[] parts = normalized.split("-");
        if (parts.length >= 2) {
            normalized = parts[0] + "-" + parts[1].trim().replaceFirst("^([0-9])$", "0$1");
        }
        return YearMonth.parse(normalized.substring(0, 7));
    }
}
