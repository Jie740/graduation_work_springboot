package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.User;
import com.clj.service.UserService;
import com.clj.mapper.UserMapper;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2026-03-02 20:07:16
*/
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    @Override
    public Result getUsersByPage(Integer pageNum, Integer pageSize) {
        Page<User> userPage = new Page<>(pageNum, pageSize);
        return Result.ok(this.lambdaQuery().page(userPage));
    }
}




