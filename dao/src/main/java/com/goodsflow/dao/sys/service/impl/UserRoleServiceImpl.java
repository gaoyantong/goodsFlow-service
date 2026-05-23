package com.goodsflow.dao.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodsflow.dao.sys.entity.UserRole;
import com.goodsflow.dao.sys.mapper.UserRoleMapper;
import com.goodsflow.dao.sys.service.IUserRoleService;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {
}
