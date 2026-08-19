package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.common.exception.BusinessException;
import com.clj.domain.SysRole;
import com.clj.domain.SysRolePermission;
import com.clj.mapper.SysRoleMapper;
import com.clj.mapper.SysRolePermissionMapper;
import com.clj.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ajie
 * @description 针对表【sys_role(系统角色表)】的数据库操作Service实现
 * @createDate 2026-08-06 23:22:14
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
        implements SysRoleService {

    private final SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public Page<SysRole> getRolesByPage(Integer pageNum, Integer pageSize) {
        return this.lambdaQuery()
                .eq(SysRole::getDeleted, 0)
                .orderByDesc(SysRole::getCreateTime)
                .page(new Page<>(pageNum, pageSize));
    }

    @Override
    public List<SysRole> getAllRoles() {
        return this.lambdaQuery()
                .eq(SysRole::getStatus, 1)
                .eq(SysRole::getDeleted, 0)
                .orderByAsc(SysRole::getCreateTime)
                .list();
    }

    @Override
    public void addRole(SysRole role) {
        // 检查角色编码是否已存在
        Long count = this.lambdaQuery()
                .eq(SysRole::getRoleCode, role.getRoleCode())
                .eq(SysRole::getDeleted, 0)
                .count();
        if (count > 0) {
            throw new BusinessException("角色编码已存在");
        }
        if (!this.save(role)) {
            throw new BusinessException("添加失败");
        }
    }

    @Override
    public void updateRole(SysRole role) {
        // 检查角色编码是否被其他角色使用
        if (role.getRoleCode() != null) {
            SysRole existing = this.lambdaQuery()
                    .eq(SysRole::getRoleCode, role.getRoleCode())
                    .ne(SysRole::getId, role.getId())
                    .eq(SysRole::getDeleted, 0)
                    .one();
            if (existing != null) {
                throw new BusinessException("角色编码已存在");
            }
        }
        if (!this.updateById(role)) {
            throw new BusinessException("修改失败");
        }
    }

    @Override
    public void deleteRole(Long id) {
        // 软删除
        if (!this.lambdaUpdate()
                .eq(SysRole::getId, id)
                .set(SysRole::getDeleted, 1)
                .update()) {
            throw new BusinessException("删除失败");
        }
    }

    @Override
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        return sysRolePermissionMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, roleId))
                .stream()
                .map(SysRolePermission::getPermissionId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 先删除该角色的所有权限
        sysRolePermissionMapper.delete(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, roleId));
        // 再批量插入新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<SysRolePermission> list = permissionIds.stream()
                    .map(permId -> {
                        SysRolePermission srp = new SysRolePermission();
                        srp.setRoleId(roleId);
                        srp.setPermissionId(permId);
                        return srp;
                    })
                    .collect(Collectors.toList());
            for (SysRolePermission srp : list) {
                sysRolePermissionMapper.insert(srp);
            }
        }
    }
}
