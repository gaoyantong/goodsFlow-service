package com.goodsflow.admin.flow.controller;

import java.time.LocalDate;
import java.time.YearMonth;

final class FlowExportFilenameUtils {
    private FlowExportFilenameUtils() {
    }

    static String inboundFilename(String exportMonth, LocalDate start, LocalDate end, boolean excludeBatchNo) {
        YearMonth month = FlowMonthUtils.parse(exportMonth);
        if (month != null) {
            return "配送数据" + month + batchNoSuffix(excludeBatchNo) + ".xlsx";
        }
        if (start != null && end != null) {
            return "配送数据" + start + "_" + end + ".xlsx";
        }
        return "配送数据.xlsx";
    }

    static String retailFilename(String exportMonth, LocalDate start, LocalDate end, boolean excludeBatchNo) {
        YearMonth month = FlowMonthUtils.parse(exportMonth);
        if (month != null) {
            return "纯销数据" + month + batchNoSuffix(excludeBatchNo) + ".xlsx";
        }
        if (start != null && end != null) {
            return "纯销数据" + start + "_" + end + ".xlsx";
        }
        return "纯销数据.xlsx";
    }

    private static String batchNoSuffix(boolean excludeBatchNo) {
        return excludeBatchNo ? "" : "-有批号";
    }
}
