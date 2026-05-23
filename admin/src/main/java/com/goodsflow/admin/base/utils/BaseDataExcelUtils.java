package com.goodsflow.admin.base.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class BaseDataExcelUtils {
    private static final DataFormatter FORMATTER = new DataFormatter();

    private BaseDataExcelUtils() {
    }

    public static void writeHeader(Workbook workbook, Sheet sheet, String[] headers) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        Row row = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
            sheet.setColumnWidth(i, 20 * 256);
        }
    }

    public static String cellValue(Row row, int index) {
        if (row == null || row.getCell(index) == null) {
            return "";
        }
        return FORMATTER.formatCellValue(row.getCell(index)).trim();
    }

    public static boolean emptyRow(Row row, int columns) {
        if (row == null) {
            return true;
        }
        for (int i = 0; i < columns; i++) {
            if (!cellValue(row, i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static boolean headerMatches(Row row, String[] headers) {
        String[] values = new String[headers.length];
        for (int i = 0; i < headers.length; i++) {
            values[i] = cellValue(row, i);
        }
        return Arrays.equals(values, headers);
    }

    public static String contentDisposition(String filename) {
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        return "attachment; filename*=UTF-8''" + encoded;
    }
}
