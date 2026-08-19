package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.SysRolePermission;
import com.clj.service.SysRolePermissionService;
import com.clj.mapper.SysRolePermissionMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【sys_role_permission(角色权限关联表)】的数据库操作Service实现
* @createDate 2026-08-06 23:22:11
*/
@Service
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission>
    implements SysRolePermissionService{

}




