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
import com.goodsflow.dao.flow.entity.StoreCollectionStore;
import com.goodsflow.dao.flow.service.IDeliveryInboundService;
import com.goodsflow.dao.flow.service.IFlowTaskService;
import com.goodsflow.dao.flow.service.IFlowTaskStoreService;
import com.goodsflow.dao.flow.service.IRetailOutboundService;
import com.goodsflow.dao.flow.service.IStoreCollectionStoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class FlowGenerationService {
    private static final double RANDOM_FLUCTUATION_RATE = 0.30;

    private final IFlowTaskService flowTaskService;
    private final IFlowTaskStoreService flowTaskStoreService;
    private final IDeliveryInboundService deliveryInboundService;
    private final IRetailOutboundService retailOutboundService;
    private final IGoodsService goodsService;
    private final IStoreService storeService;
    private final IStoreCollectionStoreService storeCollectionStoreService;

    public FlowGenerationService(
        IFlowTaskService flowTaskService,
        IFlowTaskStoreService flowTaskStoreService,
        IDeliveryInboundService deliveryInboundService,
        IRetailOutboundService retailOutboundService,
        IGoodsService goodsService,
        IStoreService storeService,
        IStoreCollectionStoreService storeCollectionStoreService
    ) {
        this.flowTaskService = flowTaskService;
        this.flowTaskStoreService = flowTaskStoreService;
        this.deliveryInboundService = deliveryInboundService;
        this.retailOutboundService = retailOutboundService;
        this.goodsService = goodsService;
        this.storeService = storeService;
        this.storeCollectionStoreService = storeCollectionStoreService;
    }

    @Transactional(rollbackFor = Exception.class)
    public synchronized ResData<FlowTask> createAndGenerate(FlowTaskModifyParams params) {
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
        if (hasStoreCollection(params) && storeCollectionIds(params).isEmpty()) {
            return ResData.fail("请选择门店集合");
        }
        List<Store> stores = loadStores(params);
        if (stores.isEmpty()) {
            return hasStoreCollection(params) ? ResData.fail("所选门店集合中没有可配送的门店") : ResData.fail("没有可配送的门店");
        }

        FlowTask task = new FlowTask();
        task.setTaskNo(nextTaskNo());
        task.setGoodsId(params.getGoodsId());
        task.setPendingDeliveryQty(params.getPendingDeliveryQty());
        task.setDeliveryStartDate(params.getDeliveryStartDate());
        task.setDeliveryEndDate(params.getDeliveryEndDate());
        task.setMaxRetailQtyPerOrder(params.getMaxRetailQtyPerOrder());
        task.setRetailDays(params.getRetailDays());
        task.setBatchNo(params.getBatchNo());
        task.setExpiryDate(params.getExpiryDate());
        task.setStoreScopeType(resolveStoreScopeType(params));
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

        List<Integer> deliveryQuantities = distributeAroundAverage(params.getPendingDeliveryQty(), stores.size());
        List<LocalDate> deliveryDates = distributeDatesAroundAverage(
            stores.size(),
            params.getDeliveryStartDate(),
            params.getDeliveryEndDate()
        );
        List<DeliveryInbound> inboundList = new ArrayList<>();
        for (int i = 0; i < stores.size(); i++) {
            int inboundQty = deliveryQuantities.get(i);
            if (inboundQty <= 0) {
                continue;
            }
            Store store = stores.get(i);
            DeliveryInbound inbound = new DeliveryInbound();
            inbound.setTaskId(task.getId());
            inbound.setBusinessDate(deliveryDates.get(i));
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
            List<Integer> dailyQuantities = distributeAroundAverage(inbound.getInboundQty(), params.getRetailDays());
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

    private String nextTaskNo() {
        FlowTask latestTask = flowTaskService.getOne(Wrappers.<FlowTask>lambdaQuery()
            .likeRight(FlowTask::getTaskNo, "FL")
            .orderByDesc(FlowTask::getTaskNo)
            .last("limit 1"));
        int latestNo = Optional.ofNullable(latestTask)
            .map(FlowTask::getTaskNo)
            .map(this::parseTaskNo)
            .orElse(0);
        return String.format("FL%06d", latestNo + 1);
    }

    private int parseTaskNo(String taskNo) {
        if (taskNo == null || !taskNo.matches("^FL\\d{6}$")) {
            return 0;
        }
        return Integer.parseInt(taskNo.substring(2));
    }

    private String resolveStoreScopeType(FlowTaskModifyParams params) {
        if (hasStoreCollection(params)) {
            return "COLLECTION";
        }
        return CollectionUtils.isEmpty(params.getStoreIds()) ? "ALL" : "SELECTED";
    }

    private List<Store> loadStores(FlowTaskModifyParams params) {
        if (hasStoreCollection(params)) {
            List<String> collectionIds = storeCollectionIds(params);
            List<String> storeIds = storeCollectionStoreService.list(Wrappers.<StoreCollectionStore>lambdaQuery()
                    .eq(StoreCollectionStore::getDeleted, false)
                    .in(StoreCollectionStore::getCollectionId, collectionIds)
                    .orderByAsc(StoreCollectionStore::getStoreId))
                .stream()
                .map(StoreCollectionStore::getStoreId)
                .distinct()
                .collect(Collectors.toList());
            if (storeIds.isEmpty()) {
                return Collections.emptyList();
            }
            return loadStoresByIds(storeIds);
        }
        return loadStoresByIds(params.getStoreIds());
    }

    private boolean hasStoreCollection(FlowTaskModifyParams params) {
        return StringUtils.hasText(params.getStoreCollectionId()) || !storeCollectionIds(params).isEmpty();
    }

    private List<String> storeCollectionIds(FlowTaskModifyParams params) {
        if (!CollectionUtils.isEmpty(params.getStoreCollectionIds())) {
            return params.getStoreCollectionIds().stream()
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());
        }
        if (!StringUtils.hasText(params.getStoreCollectionId())) {
            return Collections.emptyList();
        }
        return List.of(params.getStoreCollectionId());
    }

    private List<Store> loadStoresByIds(List<String> storeIds) {
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

    private List<Integer> distributeAroundAverage(int total, int buckets) {
        if (buckets <= 0) {
            return Collections.emptyList();
        }
        int base = total / buckets;
        int remainder = total % buckets;
        List<Integer> result = IntStream.range(0, buckets)
            .mapToObj(index -> base + (index < remainder ? 1 : 0))
            .collect(Collectors.toList());
        Collections.shuffle(result);

        int average = Math.max(1, (int) Math.round(total * 1.0 / buckets));
        int lower = total >= buckets ? Math.max(1, (int) Math.floor(average * (1 - RANDOM_FLUCTUATION_RATE))) : 0;
        int upper = Math.max(lower, (int) Math.ceil(average * (1 + RANDOM_FLUCTUATION_RATE)));
        int turns = Math.max(1, buckets / 2);
        for (int i = 0; i < turns; i++) {
            int from = ThreadLocalRandom.current().nextInt(buckets);
            int to = ThreadLocalRandom.current().nextInt(buckets);
            if (from == to || result.get(from) <= lower || result.get(to) >= upper) {
                continue;
            }
            result.set(from, result.get(from) - 1);
            result.set(to, result.get(to) + 1);
        }
        return result;
    }

    private List<LocalDate> distributeDatesAroundAverage(int total, LocalDate start, LocalDate end) {
        long days = ChronoUnit.DAYS.between(start, end) + 1;
        if (days <= 1) {
            return IntStream.range(0, total).mapToObj(index -> start).collect(Collectors.toList());
        }
        List<Integer> dateCounts = distributeAroundAverage(total, (int) days);
        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i < dateCounts.size(); i++) {
            for (int count = 0; count < dateCounts.get(i); count++) {
                dates.add(start.plusDays(i));
            }
        }
        Collections.shuffle(dates);
        return dates;
    }
}
