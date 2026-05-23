package com.goodsflow.admin.flow.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodsflow.admin.base.utils.BaseDataExcelUtils;
import com.goodsflow.admin.flow.vo.InboundSearchParams;
import com.goodsflow.common.base.ResData;
import com.goodsflow.dao.flow.entity.DeliveryInbound;
import com.goodsflow.dao.flow.entity.FlowTask;
import com.goodsflow.dao.flow.service.IDeliveryInboundService;
import com.goodsflow.dao.flow.service.IFlowTaskService;
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
@RequestMapping("/flow/inbound")
public class DeliveryInboundController {
    private static final String[] EXPORT_HEADERS = {"业务时间", "通用名", "生产厂商", "规格", "货品单位", "入库数量", "出库数量", "批号", "单位", "保管账", "有效期"};
    private static final String[] EXPORT_HEADERS_WITHOUT_BATCH_NO = {"业务时间", "通用名", "生产厂商", "规格", "货品单位", "入库数量", "出库数量", "单位", "保管账", "有效期"};
    private static final String CUSTODY_ACCOUNT = "江西康每乐药业保管账";

    private final IDeliveryInboundService deliveryInboundService;
    private final IFlowTaskService flowTaskService;

    public DeliveryInboundController(IDeliveryInboundService deliveryInboundService, IFlowTaskService flowTaskService) {
        this.deliveryInboundService = deliveryInboundService;
        this.flowTaskService = flowTaskService;
    }

    @PostMapping("list")
    public ResData<List<DeliveryInbound>> list(@RequestBody InboundSearchParams params) {
        List<String> taskIds = findTaskIds(params.getTaskNo());
        if (StringUtils.hasText(params.getTaskNo()) && taskIds.isEmpty()) {
            return ResData.success(Collections.emptyList(), 0);
        }

        LambdaQueryWrapper<DeliveryInbound> wrapper = buildWrapper(params, taskIds);
        IPage<DeliveryInbound> page = deliveryInboundService.page(new Page<>(params.getCurrent(), params.getPageSize()), wrapper);
        return ResData.success(page.getRecords(), page.getTotal());
    }

    @PostMapping("export")
    public void export(@RequestBody InboundSearchParams params, HttpServletResponse response) throws IOException {
        List<String> taskIds = findTaskIds(params.getTaskNo());
        String filename = FlowExportFilenameUtils.inboundFilename(params.getExportMonth());
        if (StringUtils.hasText(params.getTaskNo()) && taskIds.isEmpty()) {
            writeWorkbook(response, filename, Collections.emptyList(), Boolean.TRUE.equals(params.getExcludeBatchNo()));
            return;
        }
        writeWorkbook(response, filename, deliveryInboundService.list(buildWrapper(params, taskIds)), Boolean.TRUE.equals(params.getExcludeBatchNo()));
    }

    private LambdaQueryWrapper<DeliveryInbound> buildWrapper(InboundSearchParams params, List<String> taskIds) {
        LocalDate monthStart = null;
        LocalDate monthEnd = null;
        if (StringUtils.hasText(params.getExportMonth())) {
            YearMonth month = FlowMonthUtils.parse(params.getExportMonth());
            monthStart = month.atDay(1);
            monthEnd = month.atEndOfMonth();
        }
        LambdaQueryWrapper<DeliveryInbound> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(DeliveryInbound::getDeleted, false)
            .in(!CollectionUtils.isEmpty(params.getIds()), DeliveryInbound::getId, params.getIds())
            .eq(StringUtils.hasText(params.getTaskId()), DeliveryInbound::getTaskId, params.getTaskId())
            .in(StringUtils.hasText(params.getTaskNo()), DeliveryInbound::getTaskId, taskIds)
            .like(StringUtils.hasText(params.getStoreId()), DeliveryInbound::getStoreId, params.getStoreId())
            .like(StringUtils.hasText(params.getStoreName()), DeliveryInbound::getStoreName, params.getStoreName())
            .like(StringUtils.hasText(params.getGoodsId()), DeliveryInbound::getGoodsId, params.getGoodsId())
            .like(StringUtils.hasText(params.getBatchNo()), DeliveryInbound::getBatchNo, params.getBatchNo())
            .ge(params.getBusinessDateStart() != null, DeliveryInbound::getBusinessDate, params.getBusinessDateStart())
            .le(params.getBusinessDateEnd() != null, DeliveryInbound::getBusinessDate, params.getBusinessDateEnd())
            .ge(monthStart != null, DeliveryInbound::getBusinessDate, monthStart)
            .le(monthEnd != null, DeliveryInbound::getBusinessDate, monthEnd)
            .orderBy(true, FlowSortUtils.isBusinessDateAsc(params), DeliveryInbound::getBusinessDate)
            .orderByAsc(DeliveryInbound::getStoreId);
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

    private void writeWorkbook(HttpServletResponse response, String filename, List<DeliveryInbound> rows, boolean excludeBatchNo) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", BaseDataExcelUtils.contentDisposition(filename));
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            BaseDataExcelUtils.writeHeader(workbook, sheet, excludeBatchNo ? EXPORT_HEADERS_WITHOUT_BATCH_NO : EXPORT_HEADERS);
            for (int i = 0; i < rows.size(); i++) {
                DeliveryInbound item = rows.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(FlowDateUtils.formatSlashDate(item.getBusinessDate()));
                row.createCell(1).setCellValue(item.getGenericName());
                row.createCell(2).setCellValue(item.getManufacturer());
                row.createCell(3).setCellValue(item.getSpecification());
                row.createCell(4).setCellValue(item.getUnit());
                row.createCell(5).setCellValue(item.getInboundQty());
                row.createCell(6).setCellValue(0);
                if (excludeBatchNo) {
                    row.createCell(7).setCellValue(item.getStoreName());
                    row.createCell(8).setCellValue(CUSTODY_ACCOUNT);
                    row.createCell(9).setCellValue(FlowDateUtils.formatSlashDate(item.getExpiryDate()));
                } else {
                    row.createCell(7).setCellValue(item.getBatchNo());
                    row.createCell(8).setCellValue(item.getStoreName());
                    row.createCell(9).setCellValue(CUSTODY_ACCOUNT);
                    row.createCell(10).setCellValue(FlowDateUtils.formatSlashDate(item.getExpiryDate()));
                }
            }
            workbook.write(response.getOutputStream());
        }
    }

}
