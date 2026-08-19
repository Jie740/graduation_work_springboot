package com.clj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.SysRole;
import com.clj.domain.SysUserRole;

import java.util.List;

/**
 * @author ajie
 * @description 针对表【sys_user_role(用户角色关联表)】的数据库操作Service
 * @createDate 2026-08-06 23:21:56
 */
public interface SysUserRoleService extends IService<SysUserRole> {

    List<SysRole> getRolesByUserId(Long userId);

    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 查询用户的完整权限编码列表（RBAC）
     * 包含 ROLE_角色编码（供 @PreAuthorize hasRole 匹配）+ 细粒度权限编码
     *
     * @param userId 用户ID
     * @return 权限编码列表
     */
    List<String> getPermissionCodesByUserId(Long userId);
}
