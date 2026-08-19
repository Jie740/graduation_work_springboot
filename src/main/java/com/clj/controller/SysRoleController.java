package com.clj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.common.result.Result;
import com.clj.domain.SysRole;
import com.clj.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 角色管理（RBAC）
 */
@RestController
@RequestMapping("/sys-role")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @GetMapping("/page/{pageNum}/{pageSize}")
    public Result<Page<SysRole>> getRolesByPage(@PathVariable("pageNum") Integer pageNum,
                                                 @PathVariable("pageSize") Integer pageSize) {
        return Result.success(sysRoleService.getRolesByPage(pageNum, pageSize));
    }

    @GetMapping("/all")
    public Result<List<SysRole>> getAllRoles() {
        return Result.success(sysRoleService.getAllRoles());
    }

    @PostMapping("/add")
    public Result<Void> addRole(@RequestBody SysRole role) {
        sysRoleService.addRole(role);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Void> updateRole(@RequestBody SysRole role) {
        sysRoleService.updateRole(role);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteRole(@PathVariable("id") Long id) {
        sysRoleService.deleteRole(id);
        return Result.success();
    }

    @GetMapping("/{roleId}/permissions")
    public Result<List<Long>> getPermissionIdsByRoleId(@PathVariable("roleId") Long roleId) {
        return Result.success(sysRoleService.getPermissionIdsByRoleId(roleId));
    }

    @PostMapping("/assign-permissions")
    public Result<Void> assignPermissions(@RequestBody Map<String, Object> params) {
        Long roleId = Long.valueOf(params.get("roleId").toString());
        @SuppressWarnings("unchecked")
        List<Integer> permIdInts = (List<Integer>) params.get("permissionIds");
        List<Long> permissionIds = permIdInts.stream()
                .map(Long::valueOf)
                .toList();
        sysRoleService.assignPermissions(roleId, permissionIds);
        return Result.success();
    }
}
