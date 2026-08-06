package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.SysUser;
import com.clj.service.SysUserService;
import com.clj.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【sys_user(系统用户表)】的数据库操作Service实现
* @createDate 2026-08-06 23:22:04
*/
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
    implements SysUserService{

}




