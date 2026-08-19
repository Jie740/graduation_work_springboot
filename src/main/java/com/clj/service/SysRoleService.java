package com.clj.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.SysRole;

import java.util.List;

/**
 * @author ajie
 * @description 针对表【sys_role(系统角色表)】的数据库操作Service
 * @createDate 2026-08-06 23:22:14
 */
public interface SysRoleService extends IService<SysRole> {

    Page<SysRole> getRolesByPage(Integer pageNum, Integer pageSize);

    List<SysRole> getAllRoles();

    void addRole(SysRole role);

    void updateRole(SysRole role);

    void deleteRole(Long id);

    List<Long> getPermissionIdsByRoleId(Long roleId);

    void assignPermissions(Long roleId, List<Long> permissionIds);
}
