package com.goodsflow.admin.flow.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodsflow.admin.flow.service.FlowGenerationService;
import com.goodsflow.admin.flow.vo.FlowTaskModifyParams;
import com.goodsflow.admin.flow.vo.FlowTaskSearchParams;
import com.goodsflow.common.base.ResData;
import com.goodsflow.dao.flow.entity.DeliveryInbound;
import com.goodsflow.dao.flow.entity.FlowTask;
import com.goodsflow.dao.flow.entity.FlowTaskStore;
import com.goodsflow.dao.flow.entity.RetailOutbound;
import com.goodsflow.dao.flow.service.IDeliveryInboundService;
import com.goodsflow.dao.flow.service.IFlowTaskService;
import com.goodsflow.dao.flow.service.IFlowTaskStoreService;
import com.goodsflow.dao.flow.service.IRetailOutboundService;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flow/task")
public class FlowTaskController {
    private final IFlowTaskService flowTaskService;
    private final IFlowTaskStoreService flowTaskStoreService;
    private final IDeliveryInboundService deliveryInboundService;
    private final IRetailOutboundService retailOutboundService;
    private final FlowGenerationService flowGenerationService;

    public FlowTaskController(
        IFlowTaskService flowTaskService,
        IFlowTaskStoreService flowTaskStoreService,
        IDeliveryInboundService deliveryInboundService,
        IRetailOutboundService retailOutboundService,
        FlowGenerationService flowGenerationService
    ) {
        this.flowTaskService = flowTaskService;
        this.flowTaskStoreService = flowTaskStoreService;
        this.deliveryInboundService = deliveryInboundService;
        this.retailOutboundService = retailOutboundService;
        this.flowGenerationService = flowGenerationService;
    }

    @PostMapping("list")
    public ResData<List<FlowTask>> list(@RequestBody FlowTaskSearchParams params) {
        LambdaQueryWrapper<FlowTask> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(FlowTask::getDeleted, false)
            .like(StringUtils.hasText(params.getTaskNo()), FlowTask::getTaskNo, params.getTaskNo())
            .like(StringUtils.hasText(params.getGoodsId()), FlowTask::getGoodsId, params.getGoodsId())
            .like(StringUtils.hasText(params.getBatchNo()), FlowTask::getBatchNo, params.getBatchNo())
            .eq(StringUtils.hasText(params.getStatus()), FlowTask::getStatus, params.getStatus())
            .orderByDesc(FlowTask::getCreatedAt);
        IPage<FlowTask> page = flowTaskService.page(new Page<>(params.getCurrent(), params.getPageSize()), wrapper);
        return ResData.success(page.getRecords(), page.getTotal());
    }

    @PostMapping("modify")
    public ResData<FlowTask> modify(@Validated @RequestBody FlowTaskModifyParams params) {
        if (params.getId() != null) {
            return ResData.fail("数据录入记录不支持修改，请删除后重新新增");
        }
        return flowGenerationService.createAndGenerate(params);
    }

    @PostMapping("delete")
    public ResData<Void> delete(@RequestBody FlowTask task) {
        if (task.getId() == null) {
            return ResData.fail("请选择要删除的数据");
        }
        deleteByIds(List.of(task.getId()));
        return ResData.success();
    }

    @PostMapping("deleteBatch")
    public ResData<Void> deleteBatch(@RequestBody List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResData.fail("请先勾选要删除的数据");
        }
        deleteByIds(ids);
        return ResData.success();
    }

    private void deleteByIds(List<String> ids) {
        flowTaskService.update(Wrappers.<FlowTask>lambdaUpdate()
            .in(FlowTask::getId, ids)
            .set(FlowTask::getDeleted, true));
        deliveryInboundService.update(Wrappers.<DeliveryInbound>lambdaUpdate()
            .in(DeliveryInbound::getTaskId, ids)
            .set(DeliveryInbound::getDeleted, true));
        retailOutboundService.update(Wrappers.<RetailOutbound>lambdaUpdate()
            .in(RetailOutbound::getTaskId, ids)
            .set(RetailOutbound::getDeleted, true));
        flowTaskStoreService.remove(Wrappers.<FlowTaskStore>lambdaQuery()
            .in(FlowTaskStore::getTaskId, ids));
    }

    @GetMapping("stores")
    public ResData<List<FlowTaskStore>> stores(@RequestParam String taskId) {
        return ResData.success(flowTaskStoreService.list(Wrappers.<FlowTaskStore>lambdaQuery()
            .eq(FlowTaskStore::getTaskId, taskId)
            .orderByAsc(FlowTaskStore::getStoreId)));
    }
}
