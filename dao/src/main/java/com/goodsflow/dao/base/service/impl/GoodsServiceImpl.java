package com.goodsflow.dao.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodsflow.dao.base.entity.Goods;
import com.goodsflow.dao.base.mapper.GoodsMapper;
import com.goodsflow.dao.base.service.IGoodsService;
import org.springframework.stereotype.Service;

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {
}
