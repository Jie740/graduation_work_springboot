package com.clj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.common.result.Result;
import com.clj.domain.User;
import com.clj.domain.vo.UserVo;
import com.clj.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理（员工管理）：SYSTEM_ADMIN、ENTERPRISE_ADMIN
 * 个人中心接口：所有已登录用户
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //    添加用户
    @PostMapping("/addUser")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> addUser(@RequestBody User user) {
        userService.addUser(user);
        return Result.success();
    }

    //    删除用户
    @DeleteMapping("/deleteUser/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> deleteUser(@PathVariable("id") Integer id) {
        userService.deleteUser(id);
        return Result.success();
    }

    //    修改用户
    @PostMapping("/updateUser")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return Result.success();
    }

    //    分页获取用户列表
    @GetMapping("/getUsersByPage/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<User>> getUsersByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return Result.success(userService.getUsersByPage(pageNum, pageSize));
    }

    @GetMapping("/searchUsersByPage/{keyword}/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<User>> searchUsersByPage(@PathVariable("keyword") String keyword, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return Result.success(userService.searchUsersByPage(keyword, pageNum, pageSize));
    }

    @PutMapping("/updateUserStatus/{userId}/{status}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> updateUserStatus(@PathVariable("userId") Integer id, @PathVariable("status") Integer status) {
        userService.updateUserStatus(id, status);
        return Result.success();
    }

    //通过姓名和电话获取用户(承包人)
    @GetMapping("/searchUserByNameAndPhone/{name}/{phone}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Map<String, String>> searchUserByNameAndPhone(@PathVariable("name") String name, @PathVariable("phone") String phone) {
        return Result.success(userService.searchUserByNameAndPhone(name, phone));
    }

    @GetMapping("getContractorsByPage/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<User>> getContractorsByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return Result.success(userService.getContractorsByPage(pageNum, pageSize));
    }

    @GetMapping("searchContractorsByPage/{keyword}/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<User>> searchContractorsByPage(@PathVariable("keyword") String keyword, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return Result.success(userService.searchContractorsByPage(keyword, pageNum, pageSize));
    }

    // 个人中心：所有已登录用户可访问
    @GetMapping("/getUserInfo")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<UserVo> getUserInfo() {
        return Result.success(userService.getUserInfo());
    }

    @GetMapping("/getName")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Map<String, String>> getName() {
        return Result.success(userService.getName());
    }

    @PutMapping("/updatePassword")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Void> updatePassword(@RequestBody java.util.HashMap<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        userService.updatePassword(oldPassword, newPassword);
        return Result.success();
    }
}
