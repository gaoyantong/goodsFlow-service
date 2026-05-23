package com.goodsflow.admin.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodsflow.admin.base.vo.GoodsSearchParams;
import com.goodsflow.common.base.ResData;
import com.goodsflow.dao.base.entity.Goods;
import com.goodsflow.dao.base.service.IGoodsService;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/base/goods")
public class GoodsController {
    private final IGoodsService goodsService;

    public GoodsController(IGoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @PostMapping("list")
    public ResData<List<Goods>> list(@RequestBody GoodsSearchParams params) {
        LambdaQueryWrapper<Goods> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(Goods::getDeleted, false)
            .like(StringUtils.hasText(params.getGoodsId()), Goods::getGoodsId, params.getGoodsId())
            .like(StringUtils.hasText(params.getGenericName()), Goods::getGenericName, params.getGenericName())
            .orderByDesc(Goods::getCreatedAt);
        IPage<Goods> page = goodsService.page(new Page<>(params.getCurrent(), params.getPageSize()), wrapper);
        return ResData.success(page.getRecords(), page.getTotal());
    }

    @PostMapping("modify")
    public ResData<Void> modify(@Validated @RequestBody Goods goods) {
        Goods existing = goodsService.getOne(Wrappers.<Goods>lambdaQuery()
            .eq(Goods::getDeleted, false)
            .eq(Goods::getGoodsId, goods.getGoodsId())
            .last("limit 1"));
        if (existing != null && !Objects.equals(existing.getId(), goods.getId())) {
            return ResData.fail("goodsId already exists");
        }
        return goodsService.saveOrUpdate(goods) ? ResData.success() : ResData.fail("save failed");
    }

    @PostMapping("delete")
    public ResData<Void> delete(@RequestBody Goods goods) {
        boolean success = goods.getId() != null && goodsService.update(Wrappers.<Goods>lambdaUpdate()
            .eq(Goods::getId, goods.getId())
            .set(Goods::getDeleted, true));
        return success ? ResData.success() : ResData.fail("delete failed");
    }

    @PostMapping("deleteBatch")
    public ResData<Void> deleteBatch(@RequestBody List<String> ids) {
        boolean success = !ids.isEmpty() && goodsService.update(Wrappers.<Goods>lambdaUpdate()
            .in(Goods::getId, ids)
            .set(Goods::getDeleted, true));
        return success ? ResData.success() : ResData.fail("delete failed");
    }

    @GetMapping("info")
    public ResData<Goods> info(@RequestParam String id) {
        return ResData.success(goodsService.getById(id));
    }
}
