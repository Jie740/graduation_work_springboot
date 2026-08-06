package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.SysPermission;
import com.clj.service.SysPermissionService;
import com.clj.mapper.SysPermissionMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【sys_permission(系统权限表)】的数据库操作Service实现
* @createDate 2026-08-06 23:22:17
*/
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission>
    implements SysPermissionService{

}




