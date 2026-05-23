package com.goodsflow.dao.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodsflow.dao.base.entity.Store;
import com.goodsflow.dao.base.mapper.StoreMapper;
import com.goodsflow.dao.base.service.IStoreService;
import org.springframework.stereotype.Service;

@Service
public class StoreServiceImpl extends ServiceImpl<StoreMapper, Store> implements IStoreService {
}
