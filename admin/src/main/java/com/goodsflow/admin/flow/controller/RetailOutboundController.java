package com.goodsflow.admin.flow.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodsflow.admin.base.utils.BaseDataExcelUtils;
import com.goodsflow.admin.flow.vo.RetailSearchParams;
import com.goodsflow.common.base.ResData;
import com.goodsflow.dao.flow.entity.FlowTask;
import com.goodsflow.dao.flow.entity.RetailOutbound;
import com.goodsflow.dao.flow.service.IFlowTaskService;
import com.goodsflow.dao.flow.service.IRetailOutboundService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/flow/retail")
public class RetailOutboundController {
    private static final String[] EXPORT_HEADERS = {"业务日期", "单位", "通用名", "规格", "生产厂商", "货品单位", "批号", "出库数量"};
    private static final String[] EXPORT_HEADERS_WITHOUT_BATCH_NO = {"业务日期", "单位", "通用名", "规格", "生产厂商", "货品单位", "出库数量"};

    private final IRetailOutboundService retailOutboundService;
    private final IFlowTaskService flowTaskService;

    public RetailOutboundController(IRetailOutboundService retailOutboundService, IFlowTaskService flowTaskService) {
        this.retailOutboundService = retailOutboundService;
        this.flowTaskService = flowTaskService;
    }

    @PostMapping("list")
    public ResData<List<RetailOutbound>> list(@RequestBody RetailSearchParams params) {
        List<String> taskIds = findTaskIds(params.getTaskNo());
        if (StringUtils.hasText(params.getTaskNo()) && taskIds.isEmpty()) {
            return ResData.success(Collections.emptyList(), 0);
        }

        LambdaQueryWrapper<RetailOutbound> wrapper = buildWrapper(params, taskIds);
        IPage<RetailOutbound> page = retailOutboundService.page(new Page<>(params.getCurrent(), params.getPageSize()), wrapper);
        return ResData.success(page.getRecords(), page.getTotal());
    }

    @PostMapping("export")
    public void export(@RequestBody RetailSearchParams params, HttpServletResponse response) throws IOException {
        List<String> taskIds = findTaskIds(params.getTaskNo());
        boolean excludeBatchNo = Boolean.TRUE.equals(params.getExcludeBatchNo());
        String filename = FlowExportFilenameUtils.retailFilename(params.getExportMonth(), params.getBusinessDateStart(), params.getBusinessDateEnd(), excludeBatchNo);
        if (StringUtils.hasText(params.getTaskNo()) && taskIds.isEmpty()) {
            writeWorkbook(response, filename, Collections.emptyList(), excludeBatchNo);
            return;
        }
        writeWorkbook(response, filename, retailOutboundService.list(buildWrapper(params, taskIds)), excludeBatchNo);
    }

    private LambdaQueryWrapper<RetailOutbound> buildWrapper(RetailSearchParams params, List<String> taskIds) {
        LocalDate monthStart = null;
        LocalDate monthEnd = null;
        if (StringUtils.hasText(params.getExportMonth())) {
            YearMonth month = FlowMonthUtils.parse(params.getExportMonth());
            monthStart = month.atDay(1);
            monthEnd = month.atEndOfMonth();
        }
        LambdaQueryWrapper<RetailOutbound> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RetailOutbound::getDeleted, false)
            .in(!CollectionUtils.isEmpty(params.getIds()), RetailOutbound::getId, params.getIds())
            .eq(StringUtils.hasText(params.getTaskId()), RetailOutbound::getTaskId, params.getTaskId())
            .in(StringUtils.hasText(params.getTaskNo()), RetailOutbound::getTaskId, taskIds)
            .like(StringUtils.hasText(params.getStoreId()), RetailOutbound::getStoreId, params.getStoreId())
            .like(StringUtils.hasText(params.getStoreName()), RetailOutbound::getStoreName, params.getStoreName())
            .like(StringUtils.hasText(params.getGoodsId()), RetailOutbound::getGoodsId, params.getGoodsId())
            .like(StringUtils.hasText(params.getBatchNo()), RetailOutbound::getBatchNo, params.getBatchNo())
            .ge(params.getBusinessDateStart() != null, RetailOutbound::getBusinessDate, params.getBusinessDateStart())
            .le(params.getBusinessDateEnd() != null, RetailOutbound::getBusinessDate, params.getBusinessDateEnd())
            .ge(monthStart != null, RetailOutbound::getBusinessDate, monthStart)
            .le(monthEnd != null, RetailOutbound::getBusinessDate, monthEnd)
            .orderBy(true, FlowSortUtils.isBusinessDateAsc(params), RetailOutbound::getBusinessDate)
            .orderByAsc(RetailOutbound::getStoreId);
        return wrapper;
    }

    private List<String> findTaskIds(String taskNo) {
        if (!StringUtils.hasText(taskNo)) {
            return Collections.emptyList();
        }
        return flowTaskService.list(Wrappers.<FlowTask>lambdaQuery()
                .eq(FlowTask::getDeleted, false)
                .like(FlowTask::getTaskNo, taskNo))
            .stream()
            .map(FlowTask::getId)
            .collect(Collectors.toList());
    }

    private void writeWorkbook(HttpServletResponse response, String filename, List<RetailOutbound> rows, boolean excludeBatchNo) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", BaseDataExcelUtils.contentDisposition(filename));
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("纯销数据");
            BaseDataExcelUtils.writeHeader(workbook, sheet, excludeBatchNo ? EXPORT_HEADERS_WITHOUT_BATCH_NO : EXPORT_HEADERS);
            for (int i = 0; i < rows.size(); i++) {
                RetailOutbound item = rows.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(FlowDateUtils.formatSlashDate(item.getBusinessDate()));
                row.createCell(1).setCellValue(item.getStoreName());
                row.createCell(2).setCellValue(item.getGenericName());
                row.createCell(3).setCellValue(item.getSpecification());
                row.createCell(4).setCellValue(item.getManufacturer());
                row.createCell(5).setCellValue(item.getUnit());
                if (excludeBatchNo) {
                    row.createCell(6).setCellValue(item.getOutboundQty());
                } else {
                    row.createCell(6).setCellValue(item.getBatchNo());
                    row.createCell(7).setCellValue(item.getOutboundQty());
                }
            }
            workbook.write(response.getOutputStream());
        }
    }

}
