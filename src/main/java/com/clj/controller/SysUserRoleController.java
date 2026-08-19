package com.clj.controller;

import com.clj.common.result.Result;
import com.clj.domain.SysRole;
import com.clj.service.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户-角色关联管理（RBAC）
 */
@RestController
@RequestMapping("/sys-user-role")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
public class SysUserRoleController {

    private final SysUserRoleService sysUserRoleService;

    @GetMapping("/{userId}/roles")
    public Result<List<SysRole>> getRolesByUserId(@PathVariable("userId") Long userId) {
        return Result.success(sysUserRoleService.getRolesByUserId(userId));
    }

    @PostMapping("/assign")
    public Result<Void> assignRoles(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        @SuppressWarnings("unchecked")
        List<Integer> roleIdInts = (List<Integer>) params.get("roleIds");
        List<Long> roleIds = roleIdInts.stream()
                .map(Long::valueOf)
                .toList();
        sysUserRoleService.assignRoles(userId, roleIds);
        return Result.success();
    }
}
