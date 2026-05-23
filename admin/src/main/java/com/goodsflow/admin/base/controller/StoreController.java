package com.goodsflow.admin.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodsflow.admin.base.vo.StoreSearchParams;
import com.goodsflow.common.base.ResData;
import com.goodsflow.dao.base.entity.Store;
import com.goodsflow.dao.base.service.IStoreService;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/base/store")
public class StoreController {
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
            .eq(Store::getDeleted, false)
            .eq(Store::getStoreId, store.getStoreId())
            .last("limit 1"));
        if (existing != null && !Objects.equals(existing.getId(), store.getId())) {
            return ResData.fail("storeId already exists");
        }
        return storeService.saveOrUpdate(store) ? ResData.success() : ResData.fail("save failed");
    }

    @PostMapping("delete")
    public ResData<Void> delete(@RequestBody Store store) {
        boolean success = store.getId() != null && storeService.update(Wrappers.<Store>lambdaUpdate()
            .eq(Store::getId, store.getId())
            .set(Store::getDeleted, true));
        return success ? ResData.success() : ResData.fail("delete failed");
    }

    @PostMapping("deleteBatch")
    public ResData<Void> deleteBatch(@RequestBody List<String> ids) {
        boolean success = !ids.isEmpty() && storeService.update(Wrappers.<Store>lambdaUpdate()
            .in(Store::getId, ids)
            .set(Store::getDeleted, true));
        return success ? ResData.success() : ResData.fail("delete failed");
    }

    @GetMapping("info")
    public ResData<Store> info(@RequestParam String id) {
        return ResData.success(storeService.getById(id));
    }
}
