package com.goodsflow.dao.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodsflow.dao.sys.entity.Dict;
import com.goodsflow.dao.sys.mapper.DictMapper;
import com.goodsflow.dao.sys.service.IDictService;
import org.springframework.stereotype.Service;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements IDictService {
}
