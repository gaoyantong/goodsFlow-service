package com.goodsflow.admin.flow.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodsflow.admin.flow.vo.InboundSearchParams;
import com.goodsflow.common.base.ResData;
import com.goodsflow.dao.flow.entity.DeliveryInbound;
import com.goodsflow.dao.flow.service.IDeliveryInboundService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flow/inbound")
public class DeliveryInboundController {
    private final IDeliveryInboundService deliveryInboundService;

    public DeliveryInboundController(IDeliveryInboundService deliveryInboundService) {
        this.deliveryInboundService = deliveryInboundService;
    }

    @PostMapping("list")
    public ResData<List<DeliveryInbound>> list(@RequestBody InboundSearchParams params) {
        LambdaQueryWrapper<DeliveryInbound> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(DeliveryInbound::getDeleted, false)
            .eq(StringUtils.hasText(params.getTaskId()), DeliveryInbound::getTaskId, params.getTaskId())
            .like(StringUtils.hasText(params.getStoreId()), DeliveryInbound::getStoreId, params.getStoreId())
            .like(StringUtils.hasText(params.getStoreName()), DeliveryInbound::getStoreName, params.getStoreName())
            .like(StringUtils.hasText(params.getGoodsId()), DeliveryInbound::getGoodsId, params.getGoodsId())
            .like(StringUtils.hasText(params.getBatchNo()), DeliveryInbound::getBatchNo, params.getBatchNo())
            .orderByDesc(DeliveryInbound::getBusinessDate)
            .orderByAsc(DeliveryInbound::getStoreId);
        IPage<DeliveryInbound> page = deliveryInboundService.page(new Page<>(params.getCurrent(), params.getPageSize()), wrapper);
        return ResData.success(page.getRecords(), page.getTotal());
    }
}
