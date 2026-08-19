package com.clj.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.common.exception.BusinessException;
import com.clj.domain.SysRole;
import com.clj.domain.SysUser;
import com.clj.domain.SysUserRole;
import com.clj.domain.dto.SystemUserDto;
import com.clj.mapper.SysRoleMapper;
import com.clj.mapper.SysUserMapper;
import com.clj.mapper.SysUserRoleMapper;
import com.clj.security.util.SecurityUtil;
import com.clj.service.SysUserService;
import com.clj.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ajie
 * @description 针对表【sys_user(系统用户表)】的数据库操作Service实现
 * @createDate 2026-08-06 23:22:04
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {

    private final PasswordEncoder passwordEncoder;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SmsService smsService;

    /**
     * 查询拥有指定角色编码的用户ID子查询SQL片段
     */
    private static final String CONTRACTOR_ROLE_SUB_SQL =
            "SELECT sur.user_id FROM sys_user_role sur " +
            "INNER JOIN sys_role sr ON sur.role_id = sr.id " +
            "WHERE sr.role_code IN ('USER', 'ENTERPRISE_ADMIN') AND sr.deleted = 0";

    private static final String USER_ROLE_SUB_SQL =
            "SELECT sur.user_id FROM sys_user_role sur " +
            "INNER JOIN sys_role sr ON sur.role_id = sr.id " +
            "WHERE sr.role_code = 'USER' AND sr.deleted = 0";

    @Override
    public Page<SysUser> getUsersByPage(Integer pageNum, Integer pageSize) {
        return this.lambdaQuery()
                .eq(SysUser::getDeleted, 0)
                .orderByDesc(SysUser::getCreateTime)
                .page(new Page<>(pageNum, pageSize));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(SystemUserDto dto) {
        // 1. 判断注册方式：手机号注册 or 邮箱注册
        boolean isPhoneRegister = dto.getPhone() != null && !dto.getPhone().isBlank();
        boolean isEmailRegister = dto.getEmail() != null && !dto.getEmail().isBlank();

        if (isPhoneRegister) {
            // 手机号注册：检查手机号是否已注册
            Long count = this.lambdaQuery()
                    .eq(SysUser::getPhone, dto.getPhone())
                    .eq(SysUser::getDeleted, 0)
                    .count();
            if (count > 0) {
                throw new BusinessException("手机号已注册");
            }
            // 检测手机验证码（Redis中）
            smsService.verifyCode(dto.getPhone(), dto.getCode(), "REGISTER");
        } else if (isEmailRegister) {
            // 邮箱注册：检查邮箱是否已注册
            Long count = this.lambdaQuery()
                    .eq(SysUser::getEmail, dto.getEmail())
                    .eq(SysUser::getDeleted, 0)
                    .count();
            if (count > 0) {
                throw new BusinessException("邮箱已注册");
            }
            // 检测邮箱验证码（Redis中）
            smsService.verifyCode(dto.getEmail(), dto.getCode(), "REGISTER");
        } else {
            throw new BusinessException("手机号和邮箱不能同时为空");
        }

        // 2. DTO 转 SysUser
        SysUser user = new SysUser();
        BeanUtil.copyProperties(dto, user);
        // 密码 BCrypt 加密存储
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (!this.save(user)) {
            throw new BusinessException("添加用户失败");
        }

        // 3. 根据 roleCode 查询角色，写入用户-角色关联表
        SysRole role = sysRoleMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getRoleCode, dto.getRoleCode())
                        .eq(SysRole::getDeleted, 0));
        if (role == null) {
            throw new BusinessException("角色不存在: " + dto.getRoleCode());
        }

        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        sysUserRoleMapper.insert(userRole);
    }

    @Override
    public void deleteUser(Long id) {
        // 软删除
        if (!this.lambdaUpdate()
                .eq(SysUser::getId, id)
                .set(SysUser::getDeleted, 1)
                .update()) {
            throw new BusinessException("删除失败");
        }
    }

    @Override
    public void updateUser(SysUser user) {
        // 检查用户名是否被其他用户使用
        if (user.getUsername() != null) {
            SysUser existingUser = this.lambdaQuery()
                    .eq(SysUser::getUsername, user.getUsername())
                    .ne(SysUser::getId, user.getId())
                    .eq(SysUser::getDeleted, 0)
                    .one();
            if (existingUser != null) {
                throw new BusinessException("用户名已存在");
            }
        }
        // 不允许修改密码（密码修改走单独接口）
        user.setPassword(null);
        if (!this.updateById(user)) {
            throw new BusinessException("修改失败");
        }
    }

    @Override
    public Page<SysUser> searchUsersByPage(String keyword, Integer pageNum, Integer pageSize) {
        if (keyword == null || keyword.isBlank()) {
            return getUsersByPage(pageNum, pageSize);
        }
        return this.lambdaQuery()
                .eq(SysUser::getDeleted, 0)
                .and(w -> w.like(SysUser::getNickname, keyword)
                        .or().like(SysUser::getUsername, keyword)
                        .or().likeRight(SysUser::getPhone, keyword))
                .orderByDesc(SysUser::getCreateTime)
                .page(new Page<>(pageNum, pageSize));
    }

    @Override
    public void updateUserStatus(Long id, Integer status) {
        if (!this.lambdaUpdate()
                .eq(SysUser::getId, id)
                .set(SysUser::getStatus, status)
                .update()) {
            throw new BusinessException("修改失败");
        }
    }

    @Override
    public Map<String, String> searchUserByNameAndPhone(String name, String phone) {
        SysUser user = this.lambdaQuery()
                .eq(SysUser::getNickname, name)
                .eq(SysUser::getPhone, phone)
                .eq(SysUser::getDeleted, 0)
                .apply("id IN (" + USER_ROLE_SUB_SQL + ")")
                .one();
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId().toString());
        return map;
    }

    @Override
    public Page<SysUser> getContractorsByPage(Integer pageNum, Integer pageSize) {
        return this.lambdaQuery()
                .eq(SysUser::getDeleted, 0)
                .apply("id IN (" + CONTRACTOR_ROLE_SUB_SQL + ")")
                .orderByDesc(SysUser::getCreateTime)
                .page(new Page<>(pageNum, pageSize));
    }

    @Override
    public Page<SysUser> searchContractorsByPage(String keyword, Integer pageNum, Integer pageSize) {
        if (keyword == null || keyword.isBlank()) {
            return getContractorsByPage(pageNum, pageSize);
        }
        return this.lambdaQuery()
                .eq(SysUser::getDeleted, 0)
                .apply("id IN (" + CONTRACTOR_ROLE_SUB_SQL + ")")
                .and(w -> w.like(SysUser::getNickname, keyword)
                        .or().likeRight(SysUser::getPhone, keyword))
                .orderByDesc(SysUser::getCreateTime)
                .page(new Page<>(pageNum, pageSize));
    }

    @Override
    public SysUser getUserInfo() {
        SysUser user = this.lambdaQuery()
                .eq(SysUser::getId, SecurityUtil.getUserId())
                .eq(SysUser::getDeleted, 0)
                .one();
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 脱敏：不返回密码
        user.setPassword(null);
        return user;
    }

    @Override
    public Map<String, String> getName() {
        SysUser user = this.lambdaQuery()
                .eq(SysUser::getId, SecurityUtil.getUserId())
                .eq(SysUser::getDeleted, 0)
                .one();
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        Map<String, String> map = new HashMap<>();
        map.put("name", user.getNickname());
        return map;
    }

    @Override
    public void updatePassword(String oldPassword, String newPassword) {
        SysUser user = this.lambdaQuery()
                .eq(SysUser::getId, SecurityUtil.getUserId())
                .eq(SysUser::getDeleted, 0)
                .one();
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
                .eq(SysUser::getId, user.getId())
                .set(SysUser::getPassword, passwordEncoder.encode(newPassword))
                .update()) {
            throw new BusinessException("密码修改失败");
        }
    }
}
