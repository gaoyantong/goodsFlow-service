package com.goodsflow.dao.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodsflow.dao.sys.entity.RoleResource;
import com.goodsflow.dao.sys.mapper.RoleResourceMapper;
import com.goodsflow.dao.sys.service.IRoleResourceService;
import org.springframework.stereotype.Service;

@Service
public class RoleResourceServiceImpl extends ServiceImpl<RoleResourceMapper, RoleResource> implements IRoleResourceService {
}
