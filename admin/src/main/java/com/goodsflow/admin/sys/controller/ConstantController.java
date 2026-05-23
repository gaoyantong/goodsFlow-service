package com.goodsflow.admin.sys.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.goodsflow.admin.sys.vo.ConstantSearchParams;
import com.goodsflow.common.base.ResData;
import com.goodsflow.dao.sys.entity.Constant;
import com.goodsflow.dao.sys.service.IConstantService;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/sys/constant")
public class ConstantController {
    private final IConstantService constantService;

    public ConstantController(IConstantService constantService) {
        this.constantService = constantService;
    }

    @PostMapping("list")
    public ResData<List<Constant>> list(@RequestBody ConstantSearchParams params) {
        List<Constant> constants = constantService.list(Wrappers.<Constant>lambdaQuery()
            .eq(Constant::getDeleted, false)
            .like(StringUtils.hasText(params.getCode()), Constant::getCode, params.getCode())
            .like(StringUtils.hasText(params.getName()), Constant::getName, params.getName())
            .like(StringUtils.hasText(params.getParent()), Constant::getParent, params.getParent())
            .orderByAsc(Constant::getParent)
            .orderByAsc(Constant::getSortedNum));
        return ResData.success(constants, constants.size());
    }

    @PostMapping("modify")
    public ResData<Void> modify(@Validated @RequestBody Constant constant) {
        Constant existing = constantService.getOne(Wrappers.<Constant>lambdaQuery()
            .eq(Constant::getDeleted, false)
            .eq(Constant::getCode, constant.getCode())
            .last("limit 1"));
        if (existing != null && !Objects.equals(existing.getId(), constant.getId())) {
            return ResData.fail("constant code already exists");
        }
        return constantService.saveOrUpdate(constant) ? ResData.success() : ResData.fail("save failed");
    }

    @PostMapping("delete")
    public ResData<Void> delete(@RequestBody Constant constant) {
        Constant target = constant.getId() == null ? null : constantService.getById(constant.getId());
        if (target == null) {
            return ResData.fail("constant not found");
        }
        long childCount = constantService.count(Wrappers.<Constant>lambdaQuery()
            .eq(Constant::getDeleted, false)
            .eq(Constant::getParent, target.getCode()));
        if (childCount > 0) {
            return ResData.fail("delete child constants first");
        }
        return constantService.update(Wrappers.<Constant>lambdaUpdate()
            .eq(Constant::getId, target.getId())
            .set(Constant::getDeleted, true)) ? ResData.success() : ResData.fail("delete failed");
    }

    @GetMapping("info")
    public ResData<Constant> info(@RequestParam String id) {
        return ResData.success(constantService.getById(id));
    }
}
