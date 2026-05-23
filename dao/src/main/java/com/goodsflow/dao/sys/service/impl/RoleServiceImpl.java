package com.goodsflow.dao.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodsflow.dao.sys.entity.Role;
import com.goodsflow.dao.sys.mapper.RoleMapper;
import com.goodsflow.dao.sys.service.IRoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {
}
