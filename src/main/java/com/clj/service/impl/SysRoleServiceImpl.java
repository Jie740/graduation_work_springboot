package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.SysRole;
import com.clj.service.SysRoleService;
import com.clj.mapper.SysRoleMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【sys_role(系统角色表)】的数据库操作Service实现
* @createDate 2026-08-06 23:22:14
*/
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
    implements SysRoleService{

}




