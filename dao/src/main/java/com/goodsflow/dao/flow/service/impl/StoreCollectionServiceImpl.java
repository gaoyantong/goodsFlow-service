package com.goodsflow.dao.flow.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodsflow.dao.flow.entity.StoreCollection;
import com.goodsflow.dao.flow.mapper.StoreCollectionMapper;
import com.goodsflow.dao.flow.service.IStoreCollectionService;
import org.springframework.stereotype.Service;

@Service
public class StoreCollectionServiceImpl extends ServiceImpl<StoreCollectionMapper, StoreCollection> implements IStoreCollectionService {
}
