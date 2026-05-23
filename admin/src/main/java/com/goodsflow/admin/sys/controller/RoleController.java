package com.goodsflow.admin.sys.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodsflow.admin.sys.vo.ResourceTreeItem;
import com.goodsflow.admin.sys.vo.RoleResources;
import com.goodsflow.admin.sys.vo.RoleSearchParams;
import com.goodsflow.common.base.ResData;
import com.goodsflow.dao.sys.entity.Resource;
import com.goodsflow.dao.sys.entity.Role;
import com.goodsflow.dao.sys.entity.RoleResource;
import com.goodsflow.dao.sys.entity.UserRole;
import com.goodsflow.dao.sys.service.IResourceService;
import com.goodsflow.dao.sys.service.IRoleResourceService;
import com.goodsflow.dao.sys.service.IRoleService;
import com.goodsflow.dao.sys.service.IUserRoleService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sys/role")
public class RoleController {
    private final IRoleService roleService;
    private final IUserRoleService userRoleService;
    private final IResourceService resourceService;
    private final IRoleResourceService roleResourceService;

    public RoleController(IRoleService roleService, IUserRoleService userRoleService,
                          IResourceService resourceService, IRoleResourceService roleResourceService) {
        this.roleService = roleService;
        this.userRoleService = userRoleService;
        this.resourceService = resourceService;
        this.roleResourceService = roleResourceService;
    }

    @PostMapping("list")
    public ResData<List<Role>> list(@RequestBody RoleSearchParams params) {
        IPage<Role> page = roleService.page(new Page<>(params.getCurrent(), params.getPageSize()),
            Wrappers.<Role>lambdaQuery()
                .eq(Role::getDeleted, false)
                .like(StringUtils.hasText(params.getName()), Role::getName, params.getName())
                .orderByAsc(Role::getSortedNum));
        return ResData.success(page.getRecords(), page.getTotal());
    }

    @GetMapping("allList")
    public ResData<List<Role>> allList() {
        return ResData.success(roleService.list(Wrappers.<Role>lambdaQuery()
            .eq(Role::getDeleted, false)
            .orderByAsc(Role::getSortedNum)));
    }

    @PostMapping("modify")
    public ResData<Void> modify(@Validated @RequestBody Role role) {
        Role existing = roleService.getOne(Wrappers.<Role>lambdaQuery()
            .eq(Role::getDeleted, false)
            .eq(Role::getRoleCode, role.getRoleCode())
            .last("limit 1"));
        if (existing != null && !Objects.equals(existing.getId(), role.getId())) {
            return ResData.fail("role code already exists");
        }
        return roleService.saveOrUpdate(role) ? ResData.success() : ResData.fail("save failed");
    }

    @PostMapping("delete")
    public ResData<Void> delete(@RequestBody Role role) {
        if (role.getId() == null) {
            return ResData.fail("role id is required");
        }
        if (userRoleService.count(Wrappers.<UserRole>lambdaQuery()
            .eq(UserRole::getDeleted, false)
            .eq(UserRole::getRoleId, role.getId())) > 0) {
            return ResData.fail("role is assigned to users");
        }
        return roleService.update(Wrappers.<Role>lambdaUpdate()
            .eq(Role::getId, role.getId())
            .set(Role::getDeleted, true)) ? ResData.success() : ResData.fail("delete failed");
    }

    @GetMapping("info")
    public ResData<Role> info(@RequestParam String id) {
        return ResData.success(roleService.getById(id));
    }

    @PostMapping("resources")
    public ResData<List<ResourceTreeItem>> resources() {
        List<Resource> resources = resourceService.list(Wrappers.<Resource>lambdaQuery()
            .eq(Resource::getDeleted, false)
            .orderByAsc(Resource::getParentId)
            .orderByAsc(Resource::getSortedNum));
        Map<String, ResourceTreeItem> items = new LinkedHashMap<>();
        for (Resource resource : resources) {
            ResourceTreeItem item = new ResourceTreeItem();
            item.setId(resource.getId());
            item.setKey(resource.getId());
            item.setTitle(StringUtils.hasText(resource.getNameCh()) ? resource.getNameCh() : resource.getName());
            items.put(resource.getId(), item);
        }
        List<ResourceTreeItem> roots = new ArrayList<>();
        for (Resource resource : resources) {
            ResourceTreeItem item = items.get(resource.getId());
            ResourceTreeItem parent = items.get(resource.getParentId());
            if (parent == null) {
                roots.add(item);
            } else {
                parent.getChildren().add(item);
            }
        }
        return ResData.success(roots);
    }

    @PostMapping("getCheckedResources")
    public ResData<List<String>> checkedResources(@RequestBody Role role) {
        return ResData.success(roleResourceService.list(Wrappers.<RoleResource>lambdaQuery()
                .eq(RoleResource::getDeleted, false)
                .eq(RoleResource::getRoleId, role.getId()))
            .stream()
            .map(RoleResource::getResourceId)
            .collect(Collectors.toList()));
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping("allocate")
    public ResData<Void> allocate(@RequestBody RoleResources params) {
        if (!StringUtils.hasText(params.getRoleId())) {
            return ResData.fail("role id is required");
        }
        roleResourceService.remove(Wrappers.<RoleResource>lambdaQuery().eq(RoleResource::getRoleId, params.getRoleId()));
        List<String> resourceIds = params.getResourcesId() == null ? Collections.emptyList() : params.getResourcesId();
        List<RoleResource> relations = resourceIds.stream().distinct().map(resourceId -> {
            RoleResource relation = new RoleResource();
            relation.setRoleId(params.getRoleId());
            relation.setResourceId(resourceId);
            return relation;
        }).collect(Collectors.toList());
        return relations.isEmpty() || roleResourceService.saveBatch(relations) ? ResData.success() : ResData.fail("allocate failed");
    }
}
