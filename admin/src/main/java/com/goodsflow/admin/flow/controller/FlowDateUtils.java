package com.goodsflow.admin.flow.controller;

import java.time.LocalDate;

final class FlowDateUtils {
    private FlowDateUtils() {
    }

    static String formatSlashDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.getYear() + "/" + date.getMonthValue() + "/" + date.getDayOfMonth();
    }
}
