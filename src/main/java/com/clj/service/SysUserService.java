package com.clj.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.SysUser;
import com.clj.domain.dto.SystemUserDto;

import java.util.Map;

/**
 * @author ajie
 * @description 针对表【sys_user(系统用户表)】的数据库操作Service
 * @createDate 2026-08-06 23:22:04
 */
public interface SysUserService extends IService<SysUser> {

    Page<SysUser> getUsersByPage(Integer pageNum, Integer pageSize);

    void addUser(SystemUserDto dto);

    void deleteUser(Long id);

    void updateUser(SysUser user);

    Page<SysUser> searchUsersByPage(String keyword, Integer pageNum, Integer pageSize);

    void updateUserStatus(Long id, Integer status);

    Map<String, String> searchUserByNameAndPhone(String name, String phone);

    Page<SysUser> getContractorsByPage(Integer pageNum, Integer pageSize);

    Page<SysUser> searchContractorsByPage(String keyword, Integer pageNum, Integer pageSize);

    SysUser getUserInfo();

    Map<String, String> getName();

    void updatePassword(String oldPassword, String newPassword);
}
