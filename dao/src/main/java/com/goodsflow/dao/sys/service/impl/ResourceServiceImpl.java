package com.goodsflow.dao.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.goodsflow.dao.sys.entity.Resource;
import com.goodsflow.dao.sys.mapper.ResourceMapper;
import com.goodsflow.dao.sys.service.IResourceService;
import org.springframework.stereotype.Service;

@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements IResourceService {
}
