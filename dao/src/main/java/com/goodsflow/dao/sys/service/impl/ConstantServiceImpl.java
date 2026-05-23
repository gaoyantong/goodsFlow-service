package com.goodsflow.dao.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodsflow.dao.sys.entity.Constant;
import com.goodsflow.dao.sys.mapper.ConstantMapper;
import com.goodsflow.dao.sys.service.IConstantService;
import org.springframework.stereotype.Service;

@Service
public class ConstantServiceImpl extends ServiceImpl<ConstantMapper, Constant> implements IConstantService {
}
