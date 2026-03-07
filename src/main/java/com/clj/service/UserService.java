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
}