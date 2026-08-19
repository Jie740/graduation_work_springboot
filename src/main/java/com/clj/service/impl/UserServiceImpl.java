package com.clj.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.common.exception.BusinessException;
import com.clj.domain.User;
import com.clj.domain.vo.UserVo;
import com.clj.mapper.UserMapper;
import com.clj.security.util.SecurityUtil;
import com.clj.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
* @author ajie
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2026-03-02 20:07:16
*/
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<User> getUsersByPage(Integer pageNum, Integer pageSize) {
        return this.lambdaQuery().page(new Page<>(pageNum, pageSize));
    }

    @Override
    public void addUser(User user) {
        // 检查用户名是否存在
        Long count = this.lambdaQuery()
            .eq(User::getUsername, user.getUsername())
            .count();
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        // 密码 BCrypt 加密存储
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (!this.save(user)) {
            throw new BusinessException("添加失败");
        }
    }

    @Override
    public void deleteUser(Integer id) {
        if (!this.removeById(id)) {
            throw new BusinessException("删除失败");
        }
    }

    @Override
    public void updateUser(User user) {
        // 1. 检查用户名是否被其他用户使用
        if (user.getUsername() != null) {
            User existingUser = this.lambdaQuery()
                .eq(User::getUsername, user.getUsername())
                .ne(User::getUserId, user.getUserId())  // 排除当前用户自己
                .one();

            if (existingUser != null) {
                throw new BusinessException("用户名已存在");
            }
        }

        // 2. 执行更新
        if (!this.updateById(user)) {
            throw new BusinessException("修改失败");
        }
    }

    @Override
    public Page<User> searchUsersByPage(String keyword, Integer pageNum, Integer pageSize) {
        if (keyword == null) {
            return getUsersByPage(pageNum, pageSize);
        }
        return this.lambdaQuery()
                .like(User::getName, keyword)
            .page(new Page<>(pageNum, pageSize));
    }

    @Override
    public void updateUserStatus(Integer id, Integer status) {
        if (!this.lambdaUpdate()
            .eq(User::getUserId, id)
            .set(User::getStatus, status)
            .update()) {
            throw new BusinessException("修改失败");
        }
    }

    @Override
    public Map<String, String> searchUserByNameAndPhone(String name, String phone) {
        User one = this.lambdaQuery().eq(User::getName, name)
                .eq(User::getPhone, phone)
                .eq(User::getRole, "USER")
                .one();
        if (one == null) {
            throw new BusinessException("用户不存在");
        }
        Map<String, String> map = new HashMap<>();
        map.put("userId", one.getUserId().toString());
        return map;
    }

    @Override
    public Page<User> getContractorsByPage(Integer pageNum, Integer pageSize) {
        return this.lambdaQuery()
            .and(w -> w.eq(User::getRole, "USER").or().eq(User::getRole, "ENTERPRISE_ADMIN"))
            .page(new Page<>(pageNum, pageSize));
    }

    @Override
    public Page<User> searchContractorsByPage(String keyword, Integer pageNum, Integer pageSize) {
        if (keyword == null) {
            return getContractorsByPage(pageNum, pageSize);
        }
        return this.lambdaQuery()
                .ne(User::getRole, "SYSTEM_ADMIN")
                .and(w -> w.like(User::getName, keyword)
                        .or().likeRight(User::getPhone, keyword))
            .page(new Page<>(pageNum, pageSize));
    }

    @Override
    public UserVo getUserInfo() {
        // 从 SecurityContext 获取当前用户ID
        User user = this.getById(SecurityUtil.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return BeanUtil.copyProperties(user, UserVo.class);
    }

    @Override
    public Map<String, String> getName() {
        User user = this.getById(SecurityUtil.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        Map<String, String> map = new HashMap<>();
        map.put("name", user.getName());
        return map;
    }

    @Override
    public void updatePassword(String oldPassword, String newPassword) {
        User user = this.getById(SecurityUtil.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 兼容 BCrypt 与存量明文密码
        String dbPassword = user.getPassword();
        boolean oldMatched = dbPassword.startsWith("$2")
                ? passwordEncoder.matches(oldPassword, dbPassword)
                : dbPassword.equals(oldPassword);
        if (!oldMatched) {
            throw new BusinessException("旧密码错误");
        }

        // 新密码 BCrypt 加密存储
        if (!this.lambdaUpdate()
                .eq(User::getUserId, user.getUserId())
                .set(User::getPassword, passwordEncoder.encode(newPassword))
                .update()) {
            throw new BusinessException("密码修改失败");
        }
    }
}
