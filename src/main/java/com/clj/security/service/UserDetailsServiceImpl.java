package com.clj.security.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.clj.common.enums.UserStatusEnum;
import com.clj.domain.SysRole;
import com.clj.domain.SysUser;
import com.clj.mapper.SysUserMapper;
import com.clj.security.LoginUser;
import com.clj.service.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserDetailsService 实现
 * Spring Security 登录时自动调用 loadUserByUsername 查询用户
 * <p>
 * 基于系统用户表 sys_user 加载用户，角色权限来源于 RBAC 模型：
 * sys_user -> sys_user_role -> sys_role -> sys_role_permission -> sys_permission
 * 角色编码：SYSTEM_ADMIN / ENTERPRISE_ADMIN / USER
 *
 * @author ajie
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper userMapper;
    private final SysUserRoleService sysUserRoleService;

    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        // 1. 多字段查找用户：先按 username 查，再按 email、phone
        SysUser user = findByAccount(account);

        if (user == null) {
            log.warn("用户不存在: {}", account);
            throw new UsernameNotFoundException("用户不存在");
        }

        if (UserStatusEnum.DISABLED.getStatus() == user.getStatus()) {
            log.warn("用户已被禁用: {}", account);
            throw new UsernameNotFoundException("用户已被禁用");
        }

        // 2. 构建 LoginUser：权限来源于 RBAC 模型
        return buildLoginUser(user);
    }

    /**
     * 按账号查找用户：依次尝试 username → email → phone
     *
     * @param account 登录账号
     * @return 用户实体，未找到返回 null
     */
    public SysUser findByAccount(String account) {
        // 先按用户名查
        SysUser user = userMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery()
                        .eq(SysUser::getUsername, account)
                        .eq(SysUser::getDeleted, 0));
        if (user != null) {
            return user;
        }

        // 再按邮箱查
        user = userMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery()
                        .eq(SysUser::getEmail, account)
                        .eq(SysUser::getDeleted, 0));
        if (user != null) {
            return user;
        }

        // 最后按手机号查
        return userMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery()
                        .eq(SysUser::getPhone, account)
                        .eq(SysUser::getDeleted, 0));
    }

    /**
     * 按邮箱或手机号查找用户（仅限非 SYSTEM_ADMIN 角色）
     *
     * @param account 邮箱或手机号
     * @return 用户实体，未找到或角色为 SYSTEM_ADMIN 返回 null
     */
    public SysUser findByEmailOrPhoneForNonAdmin(String account) {
        SysUser user = userMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery()
                        .eq(SysUser::getEmail, account)
                        .eq(SysUser::getDeleted, 0));
        if (user == null) {
            user = userMapper.selectOne(
                    Wrappers.<SysUser>lambdaQuery()
                            .eq(SysUser::getPhone, account)
                            .eq(SysUser::getDeleted, 0));
        }
        // 管理员不允许通过邮箱/手机号登录（基于 RBAC 角色判断）
        if (user != null && hasRole(user.getId(), "SYSTEM_ADMIN")) {
            log.warn("管理员不允许使用邮箱/手机号登录: {}", account);
            return null;
        }
        return user;
    }

    /**
     * 构建 LoginUser 对象
     * 权限来源于 RBAC 模型：ROLE_角色编码 + 细粒度权限编码
     */
    public LoginUser buildLoginUser(SysUser user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setPassword(user.getPassword());
        loginUser.setNickname(user.getNickname());
        loginUser.setEmail(user.getEmail());
        loginUser.setStatus(user.getStatus());

        // 从 RBAC 模型加载角色与权限
        List<SysRole> roles = sysUserRoleService.getRolesByUserId(user.getId());
        loginUser.setRole(roles.isEmpty() ? null : roles.get(0).getRoleCode());
        loginUser.setPermissions(sysUserRoleService.getPermissionCodesByUserId(user.getId()));
        return loginUser;
    }

    /**
     * 判断用户是否拥有指定角色编码
     */
    private boolean hasRole(Long userId, String roleCode) {
        return sysUserRoleService.getRolesByUserId(userId).stream()
                .anyMatch(role -> roleCode.equals(role.getRoleCode()));
    }
}
