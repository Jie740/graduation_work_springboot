package com.clj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.common.exception.BusinessException;
import com.clj.domain.SysPermission;
import com.clj.domain.SysRole;
import com.clj.domain.SysRolePermission;
import com.clj.domain.SysUserRole;
import com.clj.mapper.SysPermissionMapper;
import com.clj.mapper.SysRoleMapper;
import com.clj.mapper.SysRolePermissionMapper;
import com.clj.mapper.SysUserRoleMapper;
import com.clj.service.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ajie
 * @description 针对表【sys_user_role(用户角色关联表)】的数据库操作Service实现
 * @createDate 2026-08-06 23:21:56
 */
@Service
@RequiredArgsConstructor
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole>
        implements SysUserRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;
    private final SysPermissionMapper sysPermissionMapper;

    @Override
    public List<SysRole> getRolesByUserId(Long userId) {
        // 查询用户关联的角色ID列表
        List<Long> roleIds = this.lambdaQuery()
                .eq(SysUserRole::getUserId, userId)
                .list()
                .stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        if (roleIds.isEmpty()) {
            return List.of();
        }

        // 查询角色信息
        return sysRoleMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .in(SysRole::getId, roleIds)
                        .eq(SysRole::getDeleted, 0)
                        .eq(SysRole::getStatus, 1));
    }

    @Override
    public List<String> getPermissionCodesByUserId(Long userId) {
        // 1. 查询用户的角色列表（仅未删除且启用的角色）
        List<SysRole> roles = getRolesByUserId(userId);
        if (roles.isEmpty()) {
            return List.of();
        }

        List<String> permissionCodes = new ArrayList<>();

        // 2. 添加 ROLE_角色编码（供 @PreAuthorize hasRole 匹配）
        roles.forEach(role -> permissionCodes.add("ROLE_" + role.getRoleCode()));

        // 3. 查询角色关联的权限ID列表
        List<Long> roleIds = roles.stream().map(SysRole::getId).collect(Collectors.toList());
        List<Long> permissionIds = sysRolePermissionMapper.selectList(
                        new LambdaQueryWrapper<SysRolePermission>()
                                .in(SysRolePermission::getRoleId, roleIds))
                .stream()
                .map(SysRolePermission::getPermissionId)
                .distinct()
                .collect(Collectors.toList());

        if (permissionIds.isEmpty()) {
            return permissionCodes;
        }

        // 4. 查询权限编码（仅未删除且启用的权限）并追加
        sysPermissionMapper.selectList(
                        new LambdaQueryWrapper<SysPermission>()
                                .in(SysPermission::getId, permissionIds)
                                .eq(SysPermission::getDeleted, 0)
                                .eq(SysPermission::getStatus, 1))
                .forEach(permission -> permissionCodes.add(permission.getPermissionCode()));

        return permissionCodes;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 先删除用户的所有角色
        this.remove(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId));

        // 再批量插入新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRole> list = roleIds.stream()
                    .map(roleId -> {
                        SysUserRole sur = new SysUserRole();
                        sur.setUserId(userId);
                        sur.setRoleId(roleId);
                        return sur;
                    })
                    .collect(Collectors.toList());
            if (!this.saveBatch(list)) {
                throw new BusinessException("分配角色失败");
            }
        }
    }
}
