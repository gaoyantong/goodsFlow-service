package com.goodsflow.admin.sys.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodsflow.admin.sys.vo.UserSearchParams;
import com.goodsflow.admin.sys.vo.UserWithRole;
import com.goodsflow.admin.sys.service.PasswordService;
import com.goodsflow.common.base.ResData;
import com.goodsflow.dao.sys.entity.Role;
import com.goodsflow.dao.sys.entity.User;
import com.goodsflow.dao.sys.entity.UserRole;
import com.goodsflow.dao.sys.service.IRoleService;
import com.goodsflow.dao.sys.service.IUserRoleService;
import com.goodsflow.dao.sys.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sys/user")
public class UserController {
    private final IUserService userService;
    private final IUserRoleService userRoleService;
    private final IRoleService roleService;
    private final PasswordService passwordService;

    public UserController(IUserService userService, IUserRoleService userRoleService, IRoleService roleService,
                          PasswordService passwordService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.roleService = roleService;
        this.passwordService = passwordService;
    }

    @PostMapping("list")
    public ResData<List<UserWithRole>> list(@RequestBody UserSearchParams params) {
        IPage<User> page = userService.page(new Page<>(params.getCurrent(), params.getPageSize()),
            Wrappers.<User>lambdaQuery()
                .eq(User::getDeleted, false)
                .like(StringUtils.hasText(params.getName()), User::getName, params.getName())
                .like(StringUtils.hasText(params.getLoginName()), User::getLoginName, params.getLoginName())
                .like(StringUtils.hasText(params.getWorkNum()), User::getWorkNum, params.getWorkNum())
                .orderByDesc(User::getCreatedAt));
        List<String> userIds = page.getRecords().stream().map(User::getId).collect(Collectors.toList());
        Map<String, UserRole> relationByUser = userIds.isEmpty() ? Collections.emptyMap() :
            userRoleService.list(Wrappers.<UserRole>lambdaQuery()
                    .eq(UserRole::getDeleted, false)
                    .in(UserRole::getUserId, userIds))
                .stream()
                .collect(Collectors.toMap(UserRole::getUserId, relation -> relation, (left, right) -> left));
        Set<String> roleIds = relationByUser.values().stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        Map<String, Role> roles = roleIds.isEmpty() ? Collections.emptyMap() :
            roleService.listByIds(roleIds).stream().collect(Collectors.toMap(Role::getId, role -> role));
        List<UserWithRole> rows = page.getRecords().stream().map(user -> {
            UserWithRole row = new UserWithRole();
            BeanUtils.copyProperties(user, row);
            row.setPassword(null);
            UserRole relation = relationByUser.get(user.getId());
            Role role = relation == null ? null : roles.get(relation.getRoleId());
            if (role != null) {
                row.setRole(role.getId());
                row.setRoleName(role.getName());
            }
            return row;
        }).collect(Collectors.toList());
        return ResData.success(rows, page.getTotal());
    }

    @GetMapping("roles")
    public ResData<List<Role>> roles() {
        return ResData.success(roleService.list(Wrappers.<Role>lambdaQuery()
            .eq(Role::getDeleted, false)
            .orderByAsc(Role::getSortedNum)));
    }

    @Transactional(rollbackFor = Exception.class)
    @PostMapping("modify")
    public ResData<Void> modify(@Validated @RequestBody UserWithRole params) {
        User existing = userService.getOne(Wrappers.<User>lambdaQuery()
            .eq(User::getDeleted, false)
            .eq(User::getLoginName, params.getLoginName())
            .last("limit 1"));
        if (existing != null && !Objects.equals(existing.getId(), params.getId())) {
            return ResData.fail("login name already exists");
        }
        User target = params;
        if (StringUtils.hasText(params.getPassword())) {
            if (params.getPassword().length() < 6 || params.getPassword().length() > 28) {
                return ResData.fail("password length must be 6 to 28");
            }
            target.setPassword(passwordService.encode(params.getPassword()));
        }
        if (StringUtils.hasText(params.getId()) && !StringUtils.hasText(params.getPassword())) {
            target.setPassword(userService.getById(params.getId()).getPassword());
        }
        if (!userService.saveOrUpdate(target)) {
            return ResData.fail("save failed");
        }
        userRoleService.remove(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUserId, target.getId()));
        if (StringUtils.hasText(params.getRole())) {
            UserRole relation = new UserRole();
            relation.setUserId(target.getId());
            relation.setRoleId(params.getRole());
            if (!userRoleService.save(relation)) {
                return ResData.fail("save role failed");
            }
        }
        return ResData.success();
    }

    @PostMapping("delete")
    public ResData<Void> delete(@RequestBody User user) {
        if (user.getId() == null) {
            return ResData.fail("user id is required");
        }
        userRoleService.update(Wrappers.<UserRole>lambdaUpdate()
            .eq(UserRole::getUserId, user.getId())
            .set(UserRole::getDeleted, true));
        return userService.update(Wrappers.<User>lambdaUpdate()
            .eq(User::getId, user.getId())
            .set(User::getDeleted, true)) ? ResData.success() : ResData.fail("delete failed");
    }

    @GetMapping("info")
    public ResData<UserWithRole> info(@RequestParam String id) {
        User user = userService.getById(id);
        if (user == null) {
            return ResData.success(null);
        }
        UserWithRole result = new UserWithRole();
        BeanUtils.copyProperties(user, result);
        result.setPassword(null);
        UserRole relation = userRoleService.getOne(Wrappers.<UserRole>lambdaQuery()
            .eq(UserRole::getDeleted, false)
            .eq(UserRole::getUserId, id)
            .last("limit 1"));
        if (relation != null) {
            result.setRole(relation.getRoleId());
        }
        return ResData.success(result);
    }
}
