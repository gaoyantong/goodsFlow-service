package com.goodsflow.admin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goodsflow.common.base.ResData;
import com.goodsflow.dao.sys.entity.Role;
import com.goodsflow.dao.sys.entity.UserRole;
import com.goodsflow.dao.sys.service.IRoleService;
import com.goodsflow.dao.sys.service.IUserRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {
    private final ObjectMapper objectMapper;
    private final IUserRoleService userRoleService;
    private final IRoleService roleService;

    public AdminAuthInterceptor(ObjectMapper objectMapper, IUserRoleService userRoleService, IRoleService roleService) {
        this.objectMapper = objectMapper;
        this.userRoleService = userRoleService;
        this.roleService = roleService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        if (request.getSession(false) != null && request.getSession(false).getAttribute(AdminSession.USER_ID) != null) {
            String userId = (String) request.getSession(false).getAttribute(AdminSession.USER_ID);
            if (isSystemManagementRequest(request) && !canManageSystem(userId)) {
                writeError(response, HttpStatus.FORBIDDEN, "无系统管理权限");
                return false;
            }
            return true;
        }
        writeError(response, HttpStatus.UNAUTHORIZED, "请先登录");
        return false;
    }

    private boolean isSystemManagementRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/sys/") && !request.getRequestURI().startsWith("/sys/admin/");
    }

    private boolean canManageSystem(String userId) {
        UserRole relation = userRoleService.getOne(com.baomidou.mybatisplus.core.toolkit.Wrappers.<UserRole>lambdaQuery()
            .eq(UserRole::getDeleted, false)
            .eq(UserRole::getUserId, userId)
            .last("limit 1"));
        Role role = relation == null ? null : roleService.getById(relation.getRoleId());
        return role != null && !"USER".equals(role.getRoleCode());
    }

    private void writeError(HttpServletResponse response, HttpStatus status, String message) throws Exception {
        response.setStatus(status.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(ResData.fail(message)));
    }
}
