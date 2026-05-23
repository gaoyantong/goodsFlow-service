package com.goodsflow.admin.flow.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.goodsflow.admin.flow.vo.FlowTaskModifyParams;
import com.goodsflow.common.base.ResData;
import com.goodsflow.dao.base.entity.Goods;
import com.goodsflow.dao.base.entity.Store;
import com.goodsflow.dao.base.service.IGoodsService;
import com.goodsflow.dao.base.service.IStoreService;
import com.goodsflow.dao.flow.entity.DeliveryInbound;
import com.goodsflow.dao.flow.entity.FlowTask;
import com.goodsflow.dao.flow.entity.FlowTaskStore;
import com.goodsflow.dao.flow.entity.RetailOutbound;
import com.goodsflow.dao.flow.service.IDeliveryInboundService;
import com.goodsflow.dao.flow.service.IFlowTaskService;
import com.goodsflow.dao.flow.service.IFlowTaskStoreService;
import com.goodsflow.dao.flow.service.IRetailOutboundService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class FlowGenerationService {
    private final IFlowTaskService flowTaskService;
    private final IFlowTaskStoreService flowTaskStoreService;
    private final IDeliveryInboundService deliveryInboundService;
    private final IRetailOutboundService retailOutboundService;
    private final IGoodsService goodsService;
    private final IStoreService storeService;

    public FlowGenerationService(
        IFlowTaskService flowTaskService,
        IFlowTaskStoreService flowTaskStoreService,
        IDeliveryInboundService deliveryInboundService,
        IRetailOutboundService retailOutboundService,
        IGoodsService goodsService,
        IStoreService storeService
    ) {
        this.flowTaskService = flowTaskService;
        this.flowTaskStoreService = flowTaskStoreService;
        this.deliveryInboundService = deliveryInboundService;
        this.retailOutboundService = retailOutboundService;
        this.goodsService = goodsService;
        this.storeService = storeService;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResData<FlowTask> createAndGenerate(FlowTaskModifyParams params) {
        if (params.getDeliveryStartDate().isAfter(params.getDeliveryEndDate())) {
            return ResData.fail("配送开始日期不能晚于配送截止日期");
        }
        Goods goods = goodsService.getOne(Wrappers.<Goods>lambdaQuery()
            .eq(Goods::getDeleted, false)
            .eq(Goods::getGoodsId, params.getGoodsId())
            .last("limit 1"));
        if (goods == null) {
            return ResData.fail("货品ID不存在");
        }
        List<Store> stores = loadStores(params.getStoreIds());
        if (stores.isEmpty()) {
            return ResData.fail("没有可配送的门店");
        }

        FlowTask task = new FlowTask();
        task.setTaskNo("FL" + System.currentTimeMillis());
        task.setGoodsId(params.getGoodsId());
        task.setPendingDeliveryQty(params.getPendingDeliveryQty());
        task.setDeliveryStartDate(params.getDeliveryStartDate());
        task.setDeliveryEndDate(params.getDeliveryEndDate());
        task.setMaxRetailQtyPerOrder(params.getMaxRetailQtyPerOrder());
        task.setRetailDays(params.getRetailDays());
        task.setBatchNo(params.getBatchNo());
        task.setExpiryDate(params.getExpiryDate());
        task.setStoreScopeType(CollectionUtils.isEmpty(params.getStoreIds()) ? "ALL" : "SELECTED");
        task.setStatus("GENERATED");
        task.setGeneratedAt(System.currentTimeMillis());
        flowTaskService.save(task);

        List<FlowTaskStore> taskStores = new ArrayList<>();
        for (Store store : stores) {
            FlowTaskStore taskStore = new FlowTaskStore();
            taskStore.setTaskId(task.getId());
            taskStore.setStoreId(store.getStoreId());
            taskStore.setStoreName(store.getStoreName());
            taskStores.add(taskStore);
        }
        flowTaskStoreService.saveBatch(taskStores);

        List<Integer> deliveryQuantities = distribute(params.getPendingDeliveryQty(), stores.size(), null);
        List<DeliveryInbound> inboundList = new ArrayList<>();
        for (int i = 0; i < stores.size(); i++) {
            int inboundQty = deliveryQuantities.get(i);
            if (inboundQty <= 0) {
                continue;
            }
            Store store = stores.get(i);
            DeliveryInbound inbound = new DeliveryInbound();
            inbound.setTaskId(task.getId());
            inbound.setBusinessDate(randomDate(params.getDeliveryStartDate(), params.getDeliveryEndDate()));
            inbound.setStoreId(store.getStoreId());
            inbound.setStoreName(store.getStoreName());
            inbound.setGoodsId(goods.getGoodsId());
            inbound.setGenericName(goods.getGenericName());
            inbound.setSpecification(goods.getSpecification());
            inbound.setManufacturer(goods.getManufacturer());
            inbound.setUnit(goods.getUnit());
            inbound.setBatchNo(params.getBatchNo());
            inbound.setExpiryDate(params.getExpiryDate());
            inbound.setInboundQty(inboundQty);
            inboundList.add(inbound);
        }
        deliveryInboundService.saveBatch(inboundList);

        List<RetailOutbound> retailList = new ArrayList<>();
        for (DeliveryInbound inbound : inboundList) {
            List<Integer> dailyQuantities = distribute(inbound.getInboundQty(), params.getRetailDays(), null);
            for (int dayIndex = 0; dayIndex < dailyQuantities.size(); dayIndex++) {
                int remainingDailyQty = dailyQuantities.get(dayIndex);
                LocalDate businessDate = inbound.getBusinessDate().plusDays(dayIndex);
                while (remainingDailyQty > 0) {
                    int outboundQty = Math.min(remainingDailyQty, params.getMaxRetailQtyPerOrder());
                    retailList.add(createRetail(task, inbound, businessDate, outboundQty));
                    remainingDailyQty -= outboundQty;
                }
            }
        }
        if (!retailList.isEmpty()) {
            retailOutboundService.saveBatch(retailList);
        }
        return ResData.success(task);
    }

    private List<Store> loadStores(List<String> storeIds) {
        if (CollectionUtils.isEmpty(storeIds)) {
            return storeService.list(Wrappers.<Store>lambdaQuery()
                .eq(Store::getDeleted, false)
                .orderByAsc(Store::getStoreId));
        }
        return storeService.list(Wrappers.<Store>lambdaQuery()
            .eq(Store::getDeleted, false)
            .in(Store::getStoreId, storeIds)
            .orderByAsc(Store::getStoreId));
    }

    private RetailOutbound createRetail(FlowTask task, DeliveryInbound inbound, LocalDate businessDate, int outboundQty) {
        RetailOutbound retail = new RetailOutbound();
        retail.setTaskId(task.getId());
        retail.setInboundId(inbound.getId());
        retail.setBusinessDate(businessDate);
        retail.setStoreId(inbound.getStoreId());
        retail.setStoreName(inbound.getStoreName());
        retail.setGoodsId(inbound.getGoodsId());
        retail.setGenericName(inbound.getGenericName());
        retail.setSpecification(inbound.getSpecification());
        retail.setManufacturer(inbound.getManufacturer());
        retail.setUnit(inbound.getUnit());
        retail.setBatchNo(inbound.getBatchNo());
        retail.setOutboundQty(outboundQty);
        return retail;
    }

    private List<Integer> distribute(int total, int buckets, Integer maxPerBucket) {
        if (buckets <= 0) {
            return Collections.emptyList();
        }
        List<Integer> result = new ArrayList<>();
        int remaining = total;
        for (int i = 0; i < buckets; i++) {
            int leftBuckets = buckets - i;
            if (leftBuckets == 1) {
                result.add(remaining);
                break;
            }
            int average = Math.max(0, remaining / leftBuckets);
            int min = remaining >= leftBuckets ? Math.max(1, (int) Math.floor(average * 0.65)) : 0;
            int max = Math.max(min, (int) Math.ceil(Math.max(1, average) * 1.35));
            max = Math.min(max, remaining);
            if (maxPerBucket != null) {
                max = Math.min(max, maxPerBucket);
            }
            int value = max <= min ? min : ThreadLocalRandom.current().nextInt(min, max + 1);
            int minForRest = remaining - value >= leftBuckets - 1 ? value : Math.max(0, remaining - (leftBuckets - 1));
            if (remaining - value < 0) {
                value = remaining;
            } else if (minForRest != value && minForRest < value) {
                value = minForRest;
            }
            result.add(value);
            remaining -= value;
        }
        return result;
    }

    private LocalDate randomDate(LocalDate start, LocalDate end) {
        long days = ChronoUnit.DAYS.between(start, end);
        if (days <= 0) {
            return start;
        }
        return start.plusDays(ThreadLocalRandom.current().nextLong(days + 1));
    }
}
