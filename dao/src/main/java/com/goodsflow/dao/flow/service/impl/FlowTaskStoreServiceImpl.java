package com.goodsflow.dao.flow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodsflow.dao.flow.entity.FlowTaskStore;
import com.goodsflow.dao.flow.mapper.FlowTaskStoreMapper;
import com.goodsflow.dao.flow.service.IFlowTaskStoreService;
import org.springframework.stereotype.Service;

@Service
public class FlowTaskStoreServiceImpl extends ServiceImpl<FlowTaskStoreMapper, FlowTaskStore> implements IFlowTaskStoreService {
}
