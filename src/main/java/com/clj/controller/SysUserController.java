package com.clj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.common.result.Result;
import com.clj.domain.SysUser;
import com.clj.domain.dto.SystemUserDto;
import com.clj.service.SysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统用户管理（RBAC）：SYSTEM_ADMIN、ENTERPRISE_ADMIN
 * 个人中心接口：所有已登录用户
 */
@RestController
@RequestMapping("/sys-user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    // ==================== 用户管理 ====================

    @PostMapping("/add")
//    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> addUser(@RequestBody @Valid SystemUserDto dto) {
        sysUserService.addUser(dto);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> deleteUser(@PathVariable("id") Long id) {
        sysUserService.deleteUser(id);
        return Result.success();
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> updateUser(@RequestBody SysUser user) {
        sysUserService.updateUser(user);
        return Result.success();
    }

    @GetMapping("/page/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<SysUser>> getUsersByPage(@PathVariable("pageNum") Integer pageNum,
                                                 @PathVariable("pageSize") Integer pageSize) {
        return Result.success(sysUserService.getUsersByPage(pageNum, pageSize));
    }

    @GetMapping("/search/{keyword}/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<SysUser>> searchUsersByPage(@PathVariable("keyword") String keyword,
                                                    @PathVariable("pageNum") Integer pageNum,
                                                    @PathVariable("pageSize") Integer pageSize) {
        return Result.success(sysUserService.searchUsersByPage(keyword, pageNum, pageSize));
    }

    @PutMapping("/status/{userId}/{status}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> updateUserStatus(@PathVariable("userId") Long id,
                                          @PathVariable("status") Integer status) {
        sysUserService.updateUserStatus(id, status);
        return Result.success();
    }

    // ==================== 承包人查询 ====================

    @GetMapping("/contractor/{name}/{phone}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Map<String, String>> searchUserByNameAndPhone(@PathVariable("name") String name,
                                                                 @PathVariable("phone") String phone) {
        return Result.success(sysUserService.searchUserByNameAndPhone(name, phone));
    }

    @GetMapping("/contractors/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<SysUser>> getContractorsByPage(@PathVariable("pageNum") Integer pageNum,
                                                       @PathVariable("pageSize") Integer pageSize) {
        return Result.success(sysUserService.getContractorsByPage(pageNum, pageSize));
    }

    @GetMapping("/contractors/search/{keyword}/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<SysUser>> searchContractorsByPage(@PathVariable("keyword") String keyword,
                                                          @PathVariable("pageNum") Integer pageNum,
                                                          @PathVariable("pageSize") Integer pageSize) {
        return Result.success(sysUserService.searchContractorsByPage(keyword, pageNum, pageSize));
    }

    // ==================== 个人中心 ====================

    @GetMapping("/info")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<SysUser> getUserInfo() {
        return Result.success(sysUserService.getUserInfo());
    }

    @GetMapping("/name")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Map<String, String>> getName() {
        return Result.success(sysUserService.getName());
    }

    @PutMapping("/password")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Void> updatePassword(@RequestBody java.util.HashMap<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        sysUserService.updatePassword(oldPassword, newPassword);
        return Result.success();
    }
}
