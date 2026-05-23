package com.goodsflow.admin.flow.controller;

import java.time.YearMonth;

final class FlowExportFilenameUtils {
    private FlowExportFilenameUtils() {
    }

    static String inboundFilename(String exportMonth) {
        YearMonth month = FlowMonthUtils.parse(exportMonth);
        if (month == null) {
            return "中国中药.xlsx";
        }
        return "中国中药" + month.getMonthValue() + "月.xlsx";
    }

    static String retailFilename(String exportMonth) {
        YearMonth month = FlowMonthUtils.parse(exportMonth);
        if (month == null) {
            return "康每乐纯销流向-有批号.xlsx";
        }
        return quarterName(month.getMonthValue()) + "康每乐纯销流向-有批号.xlsx";
    }

    private static String quarterName(int month) {
        if (month <= 3) {
            return "一季度";
        }
        if (month <= 6) {
            return "二季度";
        }
        if (month <= 9) {
            return "三季度";
        }
        return "四季度";
    }
}
