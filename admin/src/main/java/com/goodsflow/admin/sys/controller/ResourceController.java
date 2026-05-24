package com.goodsflow.admin.sys.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.goodsflow.admin.sys.vo.ResourceSearchParams;
import com.goodsflow.common.base.ResData;
import com.goodsflow.dao.sys.entity.Resource;
import com.goodsflow.dao.sys.service.IResourceService;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/sys/resource")
public class ResourceController {
    private final IResourceService resourceService;

    public ResourceController(IResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping("list")
    public ResData<List<Resource>> list(@RequestBody ResourceSearchParams params) {
        return ResData.success(resourceService.list(Wrappers.<Resource>lambdaQuery()
            .eq(Resource::getDeleted, false)
            .like(StringUtils.hasText(params.getName()), Resource::getName, params.getName())
            .like(StringUtils.hasText(params.getPath()), Resource::getPath, params.getPath())
            .orderByAsc(Resource::getParentId)
            .orderByAsc(Resource::getSortedNum)));
    }

    @PostMapping("modify")
    public ResData<Void> modify(@Validated @RequestBody Resource resource) {
        if (StringUtils.hasText(resource.getPath())) {
            Resource existing = resourceService.getOne(Wrappers.<Resource>lambdaQuery()
                .eq(Resource::getPath, resource.getPath())
                .last("limit 1"));
            if (existing != null && !Boolean.TRUE.equals(existing.getDeleted()) && !Objects.equals(existing.getId(), resource.getId())) {
                return ResData.fail("资源路径已存在");
            }
            if (existing != null && Boolean.TRUE.equals(existing.getDeleted()) && !StringUtils.hasText(resource.getId())) {
                resource.setId(existing.getId());
                resource.setDeleted(false);
            }
        }
        return resourceService.saveOrUpdate(resource) ? ResData.success() : ResData.fail("保存失败");
    }

    @PostMapping("delete")
    public ResData<Void> delete(@RequestBody Resource resource) {
        if (resource.getId() == null) {
            return ResData.fail("请选择要删除的资源");
        }
        long childCount = resourceService.count(Wrappers.<Resource>lambdaQuery()
            .eq(Resource::getDeleted, false)
            .eq(Resource::getParentId, resource.getId()));
        if (childCount > 0) {
            return ResData.fail("请先删除子资源");
        }
        return resourceService.update(Wrappers.<Resource>lambdaUpdate()
            .eq(Resource::getId, resource.getId())
            .set(Resource::getDeleted, true)) ? ResData.success() : ResData.fail("删除失败");
    }

    @GetMapping("info")
    public ResData<Resource> info(@RequestParam String id) {
        return ResData.success(resourceService.getById(id));
    }
}
