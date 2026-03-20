package com.clj.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.User;
import com.clj.service.UserService;
import com.clj.mapper.UserMapper;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

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

    @Override
    public Result addUser(User user) {
        // 检查用户名是否存在
        Long count = this.lambdaQuery()
            .eq(User::getUsername, user.getUsername())
            .count();
        if (count > 0) {
            return Result.error("用户名已存在");
        }
        return this.save(user) ? Result.ok() : Result.error("添加失败");
    }

    @Override
    public Result deleteUser(Integer id) {
        return this.removeById(id)?Result.ok():Result.error("删除失败");
    }

    @Override
    public Result updateUser(User user) {
        return this.updateById(user) ? Result.ok() : Result.error("修改失败");
    }

    @Override
    public Result searchUsersByPage(String keyword, Integer pageNum, Integer pageSize) {
        if(keyword==null){
            return getUsersByPage(pageNum, pageSize);
        }
        Page<User> page = new Page<>(pageNum, pageSize);
        Page<User> userPage = this.lambdaQuery()
//            .and(wrapper -> wrapper
//                .likeRight(User::getName, keyword)
//                .or()
//                .likeRight(User::getPhone, keyword)
//            )
                .like(User::getName,keyword)
            .page(page);
        return Result.ok(userPage);
    }

    @Override
    public Result updateUserStatus(Integer id, Integer status) {
        return this.lambdaUpdate()
            .eq(User::getUserId, id)
            .set(User::getStatus, status)
            .update() ? Result.ok() : Result.error("修改失败");
    }

    @Override
    public Result searchUserByNameAndPhone(String name, String phone) {
        User one = this.lambdaQuery().eq(User::getName, name)
                .eq(User::getPhone, phone)
                .eq(User::getRole, "user")
                .one();
        if (one == null){
            return Result.error("用户不存在");
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", one.getUserId().toString());
        return Result.ok(map);
    }

    @Override
    public Result getContractorsByPage(Integer pageNum, Integer pageSize) {
        return Result.ok(this.lambdaQuery()
            .eq(User::getRole, "user")
                        .or()
                .eq(User::getRole, "enterprise_admin")
            .page(new Page<>(pageNum, pageSize)));
    }

    @Override
    public Result searchContractorsByPage(String keyword, Integer pageNum, Integer pageSize) {
        if (keyword == null){
            return getContractorsByPage(pageNum, pageSize);
        }
        return Result.ok(this.lambdaQuery()
                        .ne(User::getRole, "system_admin")
            .like(User::getName, keyword)
                        .or()
                .likeRight(User::getPhone, keyword)
            .page(new Page<>(pageNum, pageSize)));
    }
}




