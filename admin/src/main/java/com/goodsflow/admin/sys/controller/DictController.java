package com.goodsflow.admin.sys.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.goodsflow.admin.sys.vo.DictSearchParams;
import com.goodsflow.common.base.ResData;
import com.goodsflow.dao.sys.entity.Dict;
import com.goodsflow.dao.sys.service.IDictService;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/sys/dict")
public class DictController {
    private final IDictService dictService;

    public DictController(IDictService dictService) {
        this.dictService = dictService;
    }

    @PostMapping("list")
    public ResData<List<Dict>> list(@RequestBody DictSearchParams params) {
        return ResData.success(dictService.list(Wrappers.<Dict>lambdaQuery()
            .eq(Dict::getDeleted, false)
            .like(StringUtils.hasText(params.getCode()), Dict::getCode, params.getCode())
            .like(StringUtils.hasText(params.getNameZhcn()), Dict::getNameZhcn, params.getNameZhcn())
            .like(StringUtils.hasText(params.getParent()), Dict::getParent, params.getParent())
            .orderByAsc(Dict::getParent)
            .orderByAsc(Dict::getSortedNum)));
    }

    @PostMapping("modify")
    public ResData<Void> modify(@Validated @RequestBody Dict dict) {
        Dict existing = dictService.getOne(Wrappers.<Dict>lambdaQuery()
            .eq(Dict::getDeleted, false)
            .eq(Dict::getCode, dict.getCode())
            .last("limit 1"));
        if (existing != null && !Objects.equals(existing.getId(), dict.getId())) {
            return ResData.fail("dict code already exists");
        }
        return dictService.saveOrUpdate(dict) ? ResData.success() : ResData.fail("save failed");
    }

    @PostMapping("delete")
    public ResData<Void> delete(@RequestBody Dict dict) {
        Dict target = dict.getId() == null ? null : dictService.getById(dict.getId());
        if (target == null) {
            return ResData.fail("dict not found");
        }
        long childCount = dictService.count(Wrappers.<Dict>lambdaQuery()
            .eq(Dict::getDeleted, false)
            .eq(Dict::getParent, target.getCode()));
        if (childCount > 0) {
            return ResData.fail("delete child dicts first");
        }
        return dictService.update(Wrappers.<Dict>lambdaUpdate()
            .eq(Dict::getId, target.getId())
            .set(Dict::getDeleted, true)) ? ResData.success() : ResData.fail("delete failed");
    }

    @GetMapping("info")
    public ResData<Dict> info(@RequestParam String id) {
        return ResData.success(dictService.getById(id));
    }
}
