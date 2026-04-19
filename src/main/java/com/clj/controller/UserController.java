package com.clj.controller;

import com.clj.domain.User;
import com.clj.service.UserService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

//    添加用户
    @PostMapping("/addUser")
    public Result addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

//    删除用户
    @DeleteMapping("/deleteUser/{id}")
    public Result deleteUser(@PathVariable("id") Integer id) {
        return userService.deleteUser(id);
    }
//    修改用户
    @PostMapping("/updateUser")
    public Result updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    //    分页获取用户列表
    @GetMapping("/getUsersByPage/{pageNum}/{pageSize}")
    public Result getUsersByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return userService.getUsersByPage(pageNum, pageSize);
    }

    @GetMapping("/searchUsersByPage/{keyword}/{pageNum}/{pageSize}")
    public Result searchUsersByPage(@PathVariable("keyword") String keyword, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return userService.searchUsersByPage(keyword,pageNum, pageSize);
    }

    @PutMapping("/updateUserStatus/{userId}/{status}")
    public Result updateUserStatus(@PathVariable("userId") Integer id,@PathVariable("status") Integer status) {
        return userService.updateUserStatus(id, status);
    }

    //通过姓名和电话获取用户(承包人)

    @GetMapping("/searchUserByNameAndPhone/{name}/{phone}")
    public Result searchUserByNameAndPhone(@PathVariable("name") String name,@PathVariable("phone") String phone) {
        return userService.searchUserByNameAndPhone(name, phone);
    }

    @GetMapping("getContractorsByPage/{pageNum}/{pageSize}")
    public Result getContractorsByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return userService.getContractorsByPage(pageNum, pageSize);
    }

    @GetMapping("searchContractorsByPage/{keyword}/{pageNum}/{pageSize}")
    public Result searchContractorsByPage(@PathVariable("keyword") String keyword, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return userService.searchContractorsByPage(keyword,pageNum, pageSize);
    }

    @GetMapping("/getUserInfo")
    public Result getUserInfo() {
        return userService.getUserInfo();
    }

    @GetMapping("/getName")
    public Result getName() {
        return userService.getName();
    }

    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody java.util.HashMap<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        return userService.updatePassword(oldPassword, newPassword);
    }

}
