package com.clj.service;

import com.clj.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.utils.Result;

/**
* @author ajie
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2026-03-02 20:07:16
*/
public interface UserService extends IService<User> {
    public Result getUsersByPage(Integer pageNum, Integer pageSize);

    Result addUser(User user);

    Result deleteUser(Integer id);

    Result updateUser(User user);

    Result searchUsersByPage(String keyword, Integer pageNum, Integer pageSize);

    Result updateUserStatus(Integer id, Integer status);

    Result searchUserByNameAndPhone(String name, String phone);

    Result getContractorsByPage(Integer pageNum, Integer pageSize);

    Result searchContractorsByPage(String keyword, Integer pageNum, Integer pageSize);

    Result getUserInfo();


    Result getName();
}
