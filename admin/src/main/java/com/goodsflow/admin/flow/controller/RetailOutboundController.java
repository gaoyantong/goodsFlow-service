package com.goodsflow.admin.flow.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodsflow.admin.flow.vo.RetailSearchParams;
import com.goodsflow.common.base.ResData;
import com.goodsflow.dao.flow.entity.RetailOutbound;
import com.goodsflow.dao.flow.service.IRetailOutboundService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flow/retail")
public class RetailOutboundController {
    private final IRetailOutboundService retailOutboundService;

    public RetailOutboundController(IRetailOutboundService retailOutboundService) {
        this.retailOutboundService = retailOutboundService;
    }

    @PostMapping("list")
    public ResData<List<RetailOutbound>> list(@RequestBody RetailSearchParams params) {
        LambdaQueryWrapper<RetailOutbound> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(RetailOutbound::getDeleted, false)
            .eq(StringUtils.hasText(params.getTaskId()), RetailOutbound::getTaskId, params.getTaskId())
            .like(StringUtils.hasText(params.getStoreId()), RetailOutbound::getStoreId, params.getStoreId())
            .like(StringUtils.hasText(params.getStoreName()), RetailOutbound::getStoreName, params.getStoreName())
            .like(StringUtils.hasText(params.getGoodsId()), RetailOutbound::getGoodsId, params.getGoodsId())
            .like(StringUtils.hasText(params.getBatchNo()), RetailOutbound::getBatchNo, params.getBatchNo())
            .orderByDesc(RetailOutbound::getBusinessDate)
            .orderByAsc(RetailOutbound::getStoreId);
        IPage<RetailOutbound> page = retailOutboundService.page(new Page<>(params.getCurrent(), params.getPageSize()), wrapper);
        return ResData.success(page.getRecords(), page.getTotal());
    }
}
