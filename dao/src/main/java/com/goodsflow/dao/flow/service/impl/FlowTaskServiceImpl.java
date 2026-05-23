package com.goodsflow.dao.flow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodsflow.dao.flow.entity.FlowTask;
import com.goodsflow.dao.flow.mapper.FlowTaskMapper;
import com.goodsflow.dao.flow.service.IFlowTaskService;
import org.springframework.stereotype.Service;

@Service
public class FlowTaskServiceImpl extends ServiceImpl<FlowTaskMapper, FlowTask> implements IFlowTaskService {
}
