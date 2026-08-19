package com.clj.controller;

import com.clj.common.result.Result;
import com.clj.domain.SysPermission;
import com.clj.domain.vo.PermissionTreeNode;
import com.clj.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限/菜单管理（RBAC）
 */
@RestController
@RequestMapping("/sys-permission")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
public class SysPermissionController {

    private final SysPermissionService sysPermissionService;

    @GetMapping("/tree")
    public Result<List<PermissionTreeNode>> getPermissionTree() {
        return Result.success(sysPermissionService.getPermissionTree());
    }

    @GetMapping("/menu-tree")
    public Result<List<PermissionTreeNode>> getMenuTree() {
        return Result.success(sysPermissionService.getMenuTree());
    }

    @PostMapping("/add")
    public Result<Void> addPermission(@RequestBody SysPermission permission) {
        sysPermissionService.addPermission(permission);
        return Result.success();
    }

    @PutMapping("/update")
    public Result<Void> updatePermission(@RequestBody SysPermission permission) {
        sysPermissionService.updatePermission(permission);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> deletePermission(@PathVariable("id") Long id) {
        sysPermissionService.deletePermission(id);
        return Result.success();
    }
}
