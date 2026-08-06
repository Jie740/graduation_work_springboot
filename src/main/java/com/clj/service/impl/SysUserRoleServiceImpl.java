package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.SysUserRole;
import com.clj.service.SysUserRoleService;
import com.clj.mapper.SysUserRoleMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【sys_user_role(用户角色关联表)】的数据库操作Service实现
* @createDate 2026-08-06 23:21:56
*/
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole>
    implements SysUserRoleService{

}




