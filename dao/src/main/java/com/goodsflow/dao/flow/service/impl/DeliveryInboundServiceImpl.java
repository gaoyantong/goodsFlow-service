package com.goodsflow.dao.flow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodsflow.dao.flow.entity.DeliveryInbound;
import com.goodsflow.dao.flow.mapper.DeliveryInboundMapper;
import com.goodsflow.dao.flow.service.IDeliveryInboundService;
import org.springframework.stereotype.Service;

@Service
public class DeliveryInboundServiceImpl extends ServiceImpl<DeliveryInboundMapper, DeliveryInbound> implements IDeliveryInboundService {
}
