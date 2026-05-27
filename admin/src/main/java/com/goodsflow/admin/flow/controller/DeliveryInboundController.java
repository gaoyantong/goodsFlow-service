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
import com.goodsflow.dao.flow.entity.RetailOutbound;
import com.goodsflow.dao.flow.service.IDeliveryInboundService;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/flow/inbound")
public class DeliveryInboundController {
    private static final String[] EXPORT_HEADERS = {"业务时间", "通用名", "生产厂商", "规格", "货品单位", "入库数量", "出库数量", "批号", "单位", "保管账", "有效期"};
    private static final String[] EXPORT_HEADERS_WITHOUT_BATCH_NO = {"业务时间", "通用名", "生产厂商", "规格", "货品单位", "入库数量", "出库数量", "单位", "保管账", "有效期"};
    private static final String CUSTODY_ACCOUNT = "江西康每乐药业保管账";

    private final IDeliveryInboundService deliveryInboundService;
    private final IFlowTaskService flowTaskService;
    private final IRetailOutboundService retailOutboundService;

    public DeliveryInboundController(IDeliveryInboundService deliveryInboundService, IFlowTaskService flowTaskService, IRetailOutboundService retailOutboundService) {
        this.deliveryInboundService = deliveryInboundService;
        this.flowTaskService = flowTaskService;
        this.retailOutboundService = retailOutboundService;
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
        boolean excludeBatchNo = Boolean.TRUE.equals(params.getExcludeBatchNo());
        String filename = FlowExportFilenameUtils.inboundFilename(params.getExportMonth(), params.getBusinessDateStart(), params.getBusinessDateEnd(), excludeBatchNo);
        if (StringUtils.hasText(params.getTaskNo()) && taskIds.isEmpty()) {
            writeWorkbook(response, filename, Collections.emptyList(), Collections.emptyList(), excludeBatchNo);
            return;
        }
        writeWorkbook(
            response,
            filename,
            deliveryInboundService.list(buildWrapper(params, taskIds)),
            retailOutboundService.list(buildRetailWrapper(params, taskIds)),
            excludeBatchNo
        );
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

    private LambdaQueryWrapper<RetailOutbound> buildRetailWrapper(InboundSearchParams params, List<String> taskIds) {
        LocalDate monthStart = null;
        LocalDate monthEnd = null;
        if (StringUtils.hasText(params.getExportMonth())) {
            YearMonth month = FlowMonthUtils.parse(params.getExportMonth());
            monthStart = month.atDay(1);
            monthEnd = month.atEndOfMonth();
        }
        LambdaQueryWrapper<RetailOutbound> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RetailOutbound::getDeleted, false)
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

    private void writeWorkbook(HttpServletResponse response, String filename, List<DeliveryInbound> inboundRows, List<RetailOutbound> retailRows, boolean excludeBatchNo) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", BaseDataExcelUtils.contentDisposition(filename));
        Map<String, DeliveryInbound> inboundMap = buildInboundMap(inboundRows, retailRows);
        List<ExportRow> rows = buildExportRows(inboundRows, retailRows, inboundMap);
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("配送数据");
            BaseDataExcelUtils.writeHeader(workbook, sheet, excludeBatchNo ? EXPORT_HEADERS_WITHOUT_BATCH_NO : EXPORT_HEADERS);
            for (int i = 0; i < rows.size(); i++) {
                ExportRow item = rows.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(FlowDateUtils.formatSlashDate(item.businessDate));
                row.createCell(1).setCellValue(item.genericName);
                row.createCell(2).setCellValue(item.manufacturer);
                row.createCell(3).setCellValue(item.specification);
                row.createCell(4).setCellValue(item.unit);
                row.createCell(5).setCellValue(item.inboundQty);
                row.createCell(6).setCellValue(item.outboundQty);
                if (excludeBatchNo) {
                    row.createCell(7).setCellValue(item.storeName);
                    row.createCell(8).setCellValue(CUSTODY_ACCOUNT);
                    row.createCell(9).setCellValue(FlowDateUtils.formatSlashDate(item.expiryDate));
                } else {
                    row.createCell(7).setCellValue(item.batchNo);
                    row.createCell(8).setCellValue(item.storeName);
                    row.createCell(9).setCellValue(CUSTODY_ACCOUNT);
                    row.createCell(10).setCellValue(FlowDateUtils.formatSlashDate(item.expiryDate));
                }
            }
            workbook.write(response.getOutputStream());
        }
    }

    private List<ExportRow> buildExportRows(List<DeliveryInbound> inboundRows, List<RetailOutbound> retailRows, Map<String, DeliveryInbound> inboundMap) {
        List<ExportRow> rows = new ArrayList<>();
        inboundRows.forEach(item -> rows.add(ExportRow.inbound(item)));
        retailRows.forEach(item -> rows.add(ExportRow.outbound(item, inboundMap.get(item.getInboundId()))));
        rows.sort(Comparator
            .comparing((ExportRow item) -> item.businessDate, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparingInt(item -> item.inboundQty > 0 ? 0 : 1)
            .thenComparing(item -> item.createdAt, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(item -> item.storeName, Comparator.nullsLast(String::compareTo)));
        return rows;
    }

    private Map<String, DeliveryInbound> buildInboundMap(List<DeliveryInbound> inboundRows, List<RetailOutbound> retailRows) {
        Map<String, DeliveryInbound> inboundMap = new HashMap<>();
        inboundRows.stream()
            .filter(item -> StringUtils.hasText(item.getId()))
            .forEach(item -> inboundMap.put(item.getId(), item));
        Set<String> missingInboundIds = retailRows.stream()
            .map(RetailOutbound::getInboundId)
            .filter(StringUtils::hasText)
            .filter(id -> !inboundMap.containsKey(id))
            .collect(Collectors.toSet());
        if (!missingInboundIds.isEmpty()) {
            deliveryInboundService.list(Wrappers.<DeliveryInbound>lambdaQuery()
                    .in(DeliveryInbound::getId, missingInboundIds))
                .forEach(item -> inboundMap.put(item.getId(), item));
        }
        return inboundMap;
    }

    private static class ExportRow {
        private LocalDate businessDate;
        private String genericName;
        private String manufacturer;
        private String specification;
        private String unit;
        private Integer inboundQty;
        private Integer outboundQty;
        private String batchNo;
        private String storeName;
        private LocalDate expiryDate;
        private Long createdAt;

        static ExportRow inbound(DeliveryInbound item) {
            ExportRow row = new ExportRow();
            row.businessDate = item.getBusinessDate();
            row.genericName = item.getGenericName();
            row.manufacturer = item.getManufacturer();
            row.specification = item.getSpecification();
            row.unit = item.getUnit();
            row.inboundQty = item.getInboundQty() == null ? 0 : item.getInboundQty();
            row.outboundQty = 0;
            row.batchNo = item.getBatchNo();
            row.storeName = item.getStoreName();
            row.expiryDate = item.getExpiryDate();
            row.createdAt = item.getCreatedAt();
            return row;
        }

        static ExportRow outbound(RetailOutbound item, DeliveryInbound inbound) {
            ExportRow row = new ExportRow();
            row.businessDate = item.getBusinessDate();
            row.genericName = item.getGenericName();
            row.manufacturer = item.getManufacturer();
            row.specification = item.getSpecification();
            row.unit = item.getUnit();
            row.inboundQty = 0;
            row.outboundQty = item.getOutboundQty() == null ? 0 : item.getOutboundQty();
            row.batchNo = item.getBatchNo();
            row.storeName = item.getStoreName();
            row.expiryDate = inbound == null ? null : inbound.getExpiryDate();
            row.createdAt = item.getCreatedAt();
            return row;
        }
    }

}
