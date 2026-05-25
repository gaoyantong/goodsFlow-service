package com.goodsflow.admin.flow.controller;

import java.time.LocalDate;
import java.time.YearMonth;

final class FlowExportFilenameUtils {
    private static final String[] CHINESE_MONTHS = {
        "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"
    };

    private FlowExportFilenameUtils() {
    }

    static String inboundFilename(String exportMonth, LocalDate start, LocalDate end) {
        YearMonth month = FlowMonthUtils.parse(exportMonth);
        if (month != null) {
            return "中国中药" + month.getMonthValue() + "月.xlsx";
        }
        if (start != null && end != null) {
            return "中国中药" + start + "_" + end + ".xlsx";
        }
        return "中国中药.xlsx";
    }

    static String retailFilename(String exportMonth, LocalDate start, LocalDate end) {
        YearMonth month = FlowMonthUtils.parse(exportMonth);
        if (month != null) {
            return chineseMonth(month.getMonthValue()) + "月康每乐纯销流向.xlsx";
        }
        if (start != null && end != null) {
            return start + "_" + end + "月康每乐纯销流向.xlsx";
        }
        return "康每乐纯销流向.xlsx";
    }

    private static String chineseMonth(int month) {
        if (month < 1 || month > 12) {
            return String.valueOf(month);
        }
        return CHINESE_MONTHS[month - 1];
    }
}
