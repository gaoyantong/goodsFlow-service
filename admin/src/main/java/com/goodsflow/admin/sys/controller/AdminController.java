package com.goodsflow.admin.sys.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.goodsflow.admin.config.AdminSession;
import com.goodsflow.admin.sys.service.PasswordService;
import com.goodsflow.admin.sys.vo.LoginParams;
import com.goodsflow.admin.sys.vo.MenuResource;
import com.goodsflow.common.base.ResData;
import com.goodsflow.dao.sys.entity.Resource;
import com.goodsflow.dao.sys.entity.Role;
import com.goodsflow.dao.sys.entity.RoleResource;
import com.goodsflow.dao.sys.entity.User;
import com.goodsflow.dao.sys.entity.UserRole;
import com.goodsflow.dao.sys.service.IResourceService;
import com.goodsflow.dao.sys.service.IRoleResourceService;
import com.goodsflow.dao.sys.service.IRoleService;
import com.goodsflow.dao.sys.service.IUserRoleService;
import com.goodsflow.dao.sys.service.IUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sys/admin")
public class AdminController {
    private final IUserService userService;
    private final IUserRoleService userRoleService;
    private final IRoleService roleService;
    private final IResourceService resourceService;
    private final IRoleResourceService roleResourceService;
    private final PasswordService passwordService;

    public AdminController(IUserService userService, IUserRoleService userRoleService,
                           IRoleService roleService, IResourceService resourceService,
                           IRoleResourceService roleResourceService, PasswordService passwordService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
        this.roleService = roleService;
        this.resourceService = resourceService;
        this.roleResourceService = roleResourceService;
        this.passwordService = passwordService;
    }

    @PostMapping("login")
    public ResData<User> login(@Validated @RequestBody LoginParams params, HttpServletRequest request) {
        User user = userService.getOne(Wrappers.<User>lambdaQuery()
            .eq(User::getDeleted, false)
            .eq(User::getLoginName, params.getLoginName())
            .last("limit 1"));
        if (user == null || !passwordService.matches(params.getPassword(), user.getPassword())) {
            return ResData.fail("login name or password is incorrect");
        }
        if (!user.getPassword().startsWith("sha256$")) {
            userService.update(Wrappers.<User>lambdaUpdate()
                .eq(User::getId, user.getId())
                .set(User::getPassword, passwordService.encode(params.getPassword())));
        }
        HttpSession session = request.getSession(true);
        session.setAttribute(AdminSession.USER_ID, user.getId());
        return ResData.success(withoutPassword(user));
    }

    @GetMapping("info")
    public ResData<Map<String, Object>> info(HttpSession session) {
        String userId = (String) session.getAttribute(AdminSession.USER_ID);
        User user = userService.getById(userId);
        if (user == null || Boolean.TRUE.equals(user.getDeleted())) {
            session.invalidate();
            return ResData.fail("user not found");
        }
        UserRole relation = userRoleService.getOne(Wrappers.<UserRole>lambdaQuery()
            .eq(UserRole::getDeleted, false)
            .eq(UserRole::getUserId, userId)
            .last("limit 1"));
        Role role = relation == null ? null : roleService.getById(relation.getRoleId());
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", user.getId());
        data.put("name", user.getName());
        data.put("loginName", user.getLoginName());
        data.put("workNum", user.getWorkNum());
        data.put("email", user.getEmail());
        data.put("description", user.getDescription());
        data.put("roleCode", role == null ? null : role.getRoleCode());
        data.put("roleName", role == null ? null : role.getName());
        return ResData.success(data);
    }

    @PostMapping("logout")
    public ResData<Void> logout(HttpSession session) {
        session.invalidate();
        return ResData.success();
    }

    @GetMapping("menus")
    public ResData<List<MenuResource>> menus(HttpSession session) {
        String userId = (String) session.getAttribute(AdminSession.USER_ID);
        UserRole relation = userRoleService.getOne(Wrappers.<UserRole>lambdaQuery()
            .eq(UserRole::getDeleted, false)
            .eq(UserRole::getUserId, userId)
            .last("limit 1"));
        if (relation == null) {
            return ResData.success(Collections.emptyList());
        }
        Set<String> resourceIds = roleResourceService.list(Wrappers.<RoleResource>lambdaQuery()
                .eq(RoleResource::getDeleted, false)
                .eq(RoleResource::getRoleId, relation.getRoleId()))
            .stream()
            .map(RoleResource::getResourceId)
            .collect(Collectors.toSet());
        if (resourceIds.isEmpty()) {
            return ResData.success(Collections.emptyList());
        }
        List<Resource> resources = resourceService.list(Wrappers.<Resource>lambdaQuery()
            .eq(Resource::getDeleted, false)
            .eq(Resource::getType, "MENU")
            .in(Resource::getId, resourceIds)
            .orderByAsc(Resource::getParentId)
            .orderByAsc(Resource::getSortedNum));
        Map<String, MenuResource> menus = new LinkedHashMap<>();
        for (Resource resource : resources) {
            MenuResource menu = new MenuResource();
            menu.setId(resource.getId());
            menu.setName(StringUtils.hasText(resource.getNameCh()) ? resource.getNameCh() : resource.getName());
            menu.setIcon(resource.getIcon());
            menu.setPath(resource.getPath());
            menus.put(resource.getId(), menu);
        }
        List<MenuResource> roots = new ArrayList<>();
        for (Resource resource : resources) {
            MenuResource menu = menus.get(resource.getId());
            MenuResource parent = menus.get(resource.getParentId());
            if (parent == null) {
                roots.add(menu);
            } else {
                parent.getChildren().add(menu);
            }
        }
        return ResData.success(roots);
    }

    @PostMapping("update")
    public ResData<Void> update(@RequestBody User update, HttpSession session) {
        String userId = (String) session.getAttribute(AdminSession.USER_ID);
        User target = new User();
        BeanUtils.copyProperties(update, target);
        target.setId(userId);
        target.setLoginName(null);
        if (StringUtils.hasText(update.getPassword())) {
            if (update.getPassword().length() < 6 || update.getPassword().length() > 28) {
                return ResData.fail("password length must be 6 to 28");
            }
            target.setPassword(passwordService.encode(update.getPassword()));
        } else {
            target.setPassword(null);
        }
        return userService.updateById(target) ? ResData.success() : ResData.fail("save failed");
    }

    private User withoutPassword(User source) {
        User result = new User();
        BeanUtils.copyProperties(source, result);
        result.setPassword(null);
        return result;
    }
}
