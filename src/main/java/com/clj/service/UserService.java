package com.clj.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.User;
import com.clj.domain.vo.UserVo;

import java.util.Map;

/**
* @author ajie
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2026-03-02 20:07:16
*/
public interface UserService extends IService<User> {
    Page<User> getUsersByPage(Integer pageNum, Integer pageSize);

    void addUser(User user);

    void deleteUser(Integer id);

    void updateUser(User user);

    Page<User> searchUsersByPage(String keyword, Integer pageNum, Integer pageSize);

    void updateUserStatus(Integer id, Integer status);

    Map<String, String> searchUserByNameAndPhone(String name, String phone);

    Page<User> getContractorsByPage(Integer pageNum, Integer pageSize);

    Page<User> searchContractorsByPage(String keyword, Integer pageNum, Integer pageSize);

    UserVo getUserInfo();

    Map<String, String> getName();

    void updatePassword(String oldPassword, String newPassword);
}
