package com.goodsflow.admin.flow.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodsflow.admin.base.utils.BaseDataExcelUtils;
import com.goodsflow.admin.flow.vo.StoreCollectionSearchParams;
import com.goodsflow.common.base.ResData;
import com.goodsflow.dao.base.entity.Store;
import com.goodsflow.dao.base.service.IStoreService;
import com.goodsflow.dao.flow.entity.StoreCollection;
import com.goodsflow.dao.flow.entity.StoreCollectionStore;
import com.goodsflow.dao.flow.service.IStoreCollectionService;
import com.goodsflow.dao.flow.service.IStoreCollectionStoreService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/flow/storeCollection")
public class StoreCollectionController {
    private static final String[] STORE_COLLECTION_IMPORT_HEADERS = {"门店ID"};

    private final IStoreCollectionService storeCollectionService;
    private final IStoreCollectionStoreService storeCollectionStoreService;
    private final IStoreService storeService;

    public StoreCollectionController(
        IStoreCollectionService storeCollectionService,
        IStoreCollectionStoreService storeCollectionStoreService,
        IStoreService storeService
    ) {
        this.storeCollectionService = storeCollectionService;
        this.storeCollectionStoreService = storeCollectionStoreService;
        this.storeService = storeService;
    }

    @PostMapping("list")
    public ResData<List<StoreCollection>> list(@RequestBody StoreCollectionSearchParams params) {
        LambdaQueryWrapper<StoreCollection> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(StoreCollection::getDeleted, false)
            .like(StringUtils.hasText(params.getCollectionId()), StoreCollection::getCollectionId, params.getCollectionId())
            .like(StringUtils.hasText(params.getCollectionName()), StoreCollection::getCollectionName, params.getCollectionName())
            .orderByDesc(StoreCollection::getCreatedAt);

        if (StringUtils.hasText(params.getStoreId()) || StringUtils.hasText(params.getStoreName())) {
            List<String> collectionIds = storeCollectionStoreService.list(Wrappers.<StoreCollectionStore>lambdaQuery()
                    .eq(StoreCollectionStore::getDeleted, false)
                    .like(StringUtils.hasText(params.getStoreId()), StoreCollectionStore::getStoreId, params.getStoreId())
                    .like(StringUtils.hasText(params.getStoreName()), StoreCollectionStore::getStoreName, params.getStoreName()))
                .stream()
                .map(StoreCollectionStore::getCollectionDbId)
                .distinct()
                .collect(Collectors.toList());
            if (collectionIds.isEmpty()) {
                return ResData.success(Collections.emptyList(), 0);
            }
            wrapper.in(StoreCollection::getId, collectionIds);
        }

        IPage<StoreCollection> page = storeCollectionService.page(new Page<>(params.getCurrent(), params.getPageSize()), wrapper);
        fillStores(page.getRecords());
        return ResData.success(page.getRecords(), page.getTotal());
    }

    @GetMapping("info")
    public ResData<StoreCollection> info(@RequestParam String id) {
        StoreCollection collection = storeCollectionService.getById(id);
        if (collection != null) {
            fillStores(List.of(collection));
        }
        return ResData.success(collection);
    }

    @PostMapping("modify")
    @Transactional(rollbackFor = Exception.class)
    public ResData<StoreCollection> modify(@Validated @RequestBody StoreCollection collection) {
        List<StoreCollectionStore> stores = collection.getStores();
        if (CollectionUtils.isEmpty(stores)) {
            return ResData.fail("请至少选择一个门店");
        }
        List<String> storeIds = stores.stream()
            .map(StoreCollectionStore::getStoreId)
            .filter(StringUtils::hasText)
            .distinct()
            .collect(Collectors.toList());
        if (storeIds.isEmpty()) {
            return ResData.fail("请至少选择一个门店");
        }

        Map<String, Store> storeMap = storeService.list(Wrappers.<Store>lambdaQuery()
                .eq(Store::getDeleted, false)
                .in(Store::getStoreId, storeIds))
            .stream()
            .collect(Collectors.toMap(Store::getStoreId, item -> item, (left, right) -> left));
        List<String> missingStoreIds = storeIds.stream().filter(storeId -> !storeMap.containsKey(storeId)).collect(Collectors.toList());
        if (!missingStoreIds.isEmpty()) {
            return ResData.fail("门店ID不存在：" + String.join("、", missingStoreIds));
        }

        List<StoreCollectionStore> occupiedStores = storeCollectionStoreService.list(Wrappers.<StoreCollectionStore>lambdaQuery()
            .eq(StoreCollectionStore::getDeleted, false)
            .in(StoreCollectionStore::getStoreId, storeIds)
            .ne(StringUtils.hasText(collection.getId()), StoreCollectionStore::getCollectionDbId, collection.getId()));
        if (!occupiedStores.isEmpty()) {
            String message = occupiedStores.stream()
                .map(item -> item.getStoreId() + " " + item.getStoreName())
                .distinct()
                .collect(Collectors.joining("、"));
            return ResData.fail("以下门店已存在其他集合中：" + message);
        }

        if (!StringUtils.hasText(collection.getId())) {
            collection.setCollectionId(nextCollectionId());
        }
        boolean saved = storeCollectionService.saveOrUpdate(collection);
        if (!saved) {
            return ResData.fail("保存失败");
        }

        storeCollectionStoreService.update(Wrappers.<StoreCollectionStore>lambdaUpdate()
            .eq(StoreCollectionStore::getCollectionDbId, collection.getId())
            .set(StoreCollectionStore::getDeleted, true));

        List<StoreCollectionStore> detailList = storeIds.stream().map(storeId -> {
            Store store = storeMap.get(storeId);
            StoreCollectionStore detail = new StoreCollectionStore();
            detail.setCollectionDbId(collection.getId());
            detail.setCollectionId(collection.getCollectionId());
            detail.setStoreId(store.getStoreId());
            detail.setStoreName(store.getStoreName());
            return detail;
        }).collect(Collectors.toList());
        storeCollectionStoreService.saveBatch(detailList);
        collection.setStores(detailList);
        return ResData.success(collection);
    }

    @PostMapping("delete")
    @Transactional(rollbackFor = Exception.class)
    public ResData<Void> delete(@RequestBody StoreCollection collection) {
        if (!StringUtils.hasText(collection.getId())) {
            return ResData.fail("请选择要删除的数据");
        }
        storeCollectionService.update(Wrappers.<StoreCollection>lambdaUpdate()
            .eq(StoreCollection::getId, collection.getId())
            .set(StoreCollection::getDeleted, true));
        storeCollectionStoreService.update(Wrappers.<StoreCollectionStore>lambdaUpdate()
            .eq(StoreCollectionStore::getCollectionDbId, collection.getId())
            .set(StoreCollectionStore::getDeleted, true));
        return ResData.success();
    }

    @PostMapping("importStores")
    public ResData<List<StoreCollectionStore>> importStores(@RequestParam("file") MultipartFile file) {
        Set<String> storeIds = new LinkedHashSet<>();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                String storeId = BaseDataExcelUtils.cellValue(row, 0);
                if (!StringUtils.hasText(storeId) || "门店ID".equals(storeId)) {
                    continue;
                }
                storeIds.add(storeId);
            }
        } catch (IOException e) {
            return ResData.fail("文件读取失败，请确认文件格式为 xlsx 或 xls");
        }
        if (storeIds.isEmpty()) {
            return ResData.fail("Excel中没有可导入的门店ID");
        }
        Map<String, Store> storeMap = storeService.list(Wrappers.<Store>lambdaQuery()
                .eq(Store::getDeleted, false)
                .in(Store::getStoreId, storeIds))
            .stream()
            .collect(Collectors.toMap(Store::getStoreId, item -> item, (left, right) -> left));
        List<String> missingStoreIds = storeIds.stream().filter(storeId -> !storeMap.containsKey(storeId)).collect(Collectors.toList());
        if (!missingStoreIds.isEmpty()) {
            return ResData.fail("门店ID不存在：" + String.join("、", missingStoreIds));
        }
        List<StoreCollectionStore> detailList = storeIds.stream().map(storeId -> {
            Store store = storeMap.get(storeId);
            StoreCollectionStore detail = new StoreCollectionStore();
            detail.setStoreId(store.getStoreId());
            detail.setStoreName(store.getStoreName());
            return detail;
        }).collect(Collectors.toList());
        return ResData.success(detailList);
    }

    @GetMapping("template")
    public void template(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", BaseDataExcelUtils.contentDisposition("门店集合导入模板.xlsx"));
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("门店ID");
            BaseDataExcelUtils.writeHeader(workbook, sheet, STORE_COLLECTION_IMPORT_HEADERS);
            workbook.write(response.getOutputStream());
        }
    }

    private void fillStores(List<StoreCollection> collections) {
        if (CollectionUtils.isEmpty(collections)) {
            return;
        }
        List<String> collectionIds = collections.stream().map(StoreCollection::getId).collect(Collectors.toList());
        Map<String, List<StoreCollectionStore>> storeMap = storeCollectionStoreService.list(Wrappers.<StoreCollectionStore>lambdaQuery()
                .eq(StoreCollectionStore::getDeleted, false)
                .in(StoreCollectionStore::getCollectionDbId, collectionIds)
                .orderByAsc(StoreCollectionStore::getStoreId))
            .stream()
            .collect(Collectors.groupingBy(StoreCollectionStore::getCollectionDbId));
        collections.forEach(collection -> collection.setStores(storeMap.getOrDefault(collection.getId(), new ArrayList<>())));
    }

    private String nextCollectionId() {
        StoreCollection latest = storeCollectionService.getOne(Wrappers.<StoreCollection>lambdaQuery()
            .likeRight(StoreCollection::getCollectionId, "SC")
            .orderByDesc(StoreCollection::getCollectionId)
            .last("limit 1"));
        int latestNo = Optional.ofNullable(latest)
            .map(StoreCollection::getCollectionId)
            .map(this::parseCollectionNo)
            .orElse(0);
        return String.format("SC%06d", latestNo + 1);
    }

    private int parseCollectionNo(String collectionId) {
        if (collectionId == null || !collectionId.matches("^SC\\d{6}$")) {
            return 0;
        }
        return Integer.parseInt(collectionId.substring(2));
    }
}
