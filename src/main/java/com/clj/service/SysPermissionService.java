package com.clj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.SysPermission;
import com.clj.domain.vo.PermissionTreeNode;

import java.util.List;

/**
 * @author ajie
 * @description 针对表【sys_permission(系统权限表)】的数据库操作Service
 * @createDate 2026-08-06 23:22:17
 */
public interface SysPermissionService extends IService<SysPermission> {

    List<PermissionTreeNode> getPermissionTree();

    List<PermissionTreeNode> getMenuTree();

    void addPermission(SysPermission permission);

    void updatePermission(SysPermission permission);

    void deletePermission(Long id);
}
