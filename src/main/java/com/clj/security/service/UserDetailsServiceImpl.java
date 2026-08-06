package com.clj.security.service;

import com.clj.common.enums.RoleEnum;
import com.clj.common.enums.UserStatusEnum;
import com.clj.security.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UserDetailsService 实现
 * Spring Security 登录时自动调用 loadUserByUsername 查询用户
 * <p>
 * 使用自定义 SQL 关联查询，一次性获取用户、角色、权限信息
 *
 * @author ajie
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper userMapper;

    private final SysRoleMapper roleMapper;

    private final SysPermissionMapper permissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 查询用户
        SysUser user = userMapper.selectByUsername(username);

        if (user == null) {
            log.warn("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在");
        }

        if (UserStatusEnum.DISABLED.getStatus() == user.getStatus()) {
            log.warn("用户已被禁用: {}", username);
            throw new UsernameNotFoundException("用户已被禁用");
        }

        // 2. 通过关联表查询用户角色（一条 SQL JOIN 完成）
        List<SysRole> roles = roleMapper.selectByUserId(user.getId());
        List<String> roleCodes = roles.stream()
                .map(SysRole::getRoleCode)
                .collect(Collectors.toList());

        // 3. 通过关联表查询用户权限（一条 SQL JOIN 完成）
        List<SysPermission> permissions = permissionMapper.selectByUserId(user.getId());
        List<String> permissionCodes = permissions.stream()
                .map(SysPermission::getPermissionCode)
                .collect(Collectors.toList());

        // 4. 组装权限列表（角色编码 + 权限编码）
        List<String> authorities = new ArrayList<>();
        // 角色编码加入（Spring Security hasRole() 会识别 ROLE_ 前缀）
        authorities.addAll(roleCodes);
        // 权限编码直接加入
        authorities.addAll(permissionCodes);

        // 5. 如果是超级管理员，授予所有权限通配符
        if (roleCodes.contains(RoleEnum.ADMIN.getCode())) {
            authorities.add("*:*:*");
        }

        // 6. 构建 LoginUser
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setPassword(user.getPassword());
        loginUser.setNickname(user.getNickname());
        loginUser.setEmail(user.getEmail());
        loginUser.setStatus(user.getStatus());
        loginUser.setPermissions(authorities);

        log.info("用户加载成功: {}，角色: {}，权限: {}", username, roleCodes, permissionCodes);
        return loginUser;
    }
}
