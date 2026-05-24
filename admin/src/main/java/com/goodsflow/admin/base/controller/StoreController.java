package com.goodsflow.admin.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodsflow.admin.base.utils.BaseDataExcelUtils;
import com.goodsflow.admin.base.vo.StoreSearchParams;
import com.goodsflow.common.base.ResData;
import com.goodsflow.dao.base.entity.Store;
import com.goodsflow.dao.base.service.IStoreService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/base/store")
public class StoreController {
    private static final String[] STORE_HEADERS = {"门店ID", "门店"};

    private final IStoreService storeService;

    public StoreController(IStoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping("list")
    public ResData<List<Store>> list(@RequestBody StoreSearchParams params) {
        LambdaQueryWrapper<Store> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Store::getDeleted, false)
            .like(StringUtils.hasText(params.getStoreId()), Store::getStoreId, params.getStoreId())
            .like(StringUtils.hasText(params.getStoreName()), Store::getStoreName, params.getStoreName())
            .orderByDesc(Store::getCreatedAt);
        IPage<Store> page = storeService.page(new Page<>(params.getCurrent(), params.getPageSize()), wrapper);
        return ResData.success(page.getRecords(), page.getTotal());
    }

    @PostMapping("modify")
    public ResData<Void> modify(@Validated @RequestBody Store store) {
        Store existing = storeService.getOne(Wrappers.<Store>lambdaQuery()
            .eq(Store::getStoreId, store.getStoreId())
            .last("limit 1"));
        if (existing != null && !Boolean.TRUE.equals(existing.getDeleted()) && !Objects.equals(existing.getId(), store.getId())) {
            return ResData.fail("门店ID已存在");
        }
        if (existing != null && Boolean.TRUE.equals(existing.getDeleted()) && !StringUtils.hasText(store.getId())) {
            store.setId(existing.getId());
            store.setDeleted(false);
        }
        return storeService.saveOrUpdate(store) ? ResData.success() : ResData.fail("保存失败");
    }

    @PostMapping("delete")
    public ResData<Void> delete(@RequestBody Store store) {
        boolean success = store.getId() != null && storeService.update(Wrappers.<Store>lambdaUpdate()
            .eq(Store::getId, store.getId())
            .set(Store::getDeleted, true));
        return success ? ResData.success() : ResData.fail("删除失败");
    }

    @PostMapping("deleteBatch")
    public ResData<Void> deleteBatch(@RequestBody List<String> ids) {
        boolean success = !ids.isEmpty() && storeService.update(Wrappers.<Store>lambdaUpdate()
            .in(Store::getId, ids)
            .set(Store::getDeleted, true));
        return success ? ResData.success() : ResData.fail("删除失败");
    }

    @GetMapping("info")
    public ResData<Store> info(@RequestParam String id) {
        return ResData.success(storeService.getById(id));
    }

    @GetMapping("template")
    public void template(HttpServletResponse response) throws IOException {
        writeStoreWorkbook(response, "门店资料导入模板.xlsx", List.of());
    }

    @PostMapping("export")
    public void export(@RequestBody StoreSearchParams params, HttpServletResponse response) throws IOException {
        LambdaQueryWrapper<Store> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Store::getDeleted, false)
            .in(!CollectionUtils.isEmpty(params.getIds()), Store::getId, params.getIds())
            .like(StringUtils.hasText(params.getStoreId()), Store::getStoreId, params.getStoreId())
            .like(StringUtils.hasText(params.getStoreName()), Store::getStoreName, params.getStoreName())
            .orderByDesc(Store::getCreatedAt);
        writeStoreWorkbook(response, "门店资料.xlsx", storeService.list(wrapper));
    }

    @PostMapping("import")
    public ResData<String> importExcel(@RequestParam("file") MultipartFile file) {
        int saved = 0;
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            if (!BaseDataExcelUtils.headerMatches(sheet.getRow(0), STORE_HEADERS)) {
                return ResData.fail("导入模板不正确，请先下载模板");
            }
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (BaseDataExcelUtils.emptyRow(row, STORE_HEADERS.length)) {
                    continue;
                }
                String storeId = BaseDataExcelUtils.cellValue(row, 0);
                String storeName = BaseDataExcelUtils.cellValue(row, 1);
                if (!StringUtils.hasText(storeId) || !StringUtils.hasText(storeName)) {
                    return ResData.fail("第" + (i + 1) + "行存在必填项为空");
                }
                Store store = storeService.getOne(Wrappers.<Store>lambdaQuery()
                    .eq(Store::getStoreId, storeId)
                    .last("limit 1"));
                if (store == null) {
                    store = new Store();
                    store.setStoreId(storeId);
                } else if (Boolean.TRUE.equals(store.getDeleted())) {
                    store.setDeleted(false);
                }
                store.setStoreName(storeName);
                storeService.saveOrUpdate(store);
                saved++;
            }
            return ResData.success("导入成功：" + saved + "条");
        } catch (IOException e) {
            return ResData.fail("文件读取失败，请确认文件格式为 xlsx 或 xls");
        }
    }

    private void writeStoreWorkbook(HttpServletResponse response, String filename, List<Store> storeList) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", BaseDataExcelUtils.contentDisposition(filename));
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("门店资料");
            BaseDataExcelUtils.writeHeader(workbook, sheet, STORE_HEADERS);
            for (int i = 0; i < storeList.size(); i++) {
                Store store = storeList.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(store.getStoreId());
                row.createCell(1).setCellValue(store.getStoreName());
            }
            workbook.write(response.getOutputStream());
        }
    }
}
