package com.goodsflow.dao.flow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodsflow.dao.flow.entity.RetailOutbound;
import com.goodsflow.dao.flow.mapper.RetailOutboundMapper;
import com.goodsflow.dao.flow.service.IRetailOutboundService;
import org.springframework.stereotype.Service;

@Service
public class RetailOutboundServiceImpl extends ServiceImpl<RetailOutboundMapper, RetailOutbound> implements IRetailOutboundService {
}
