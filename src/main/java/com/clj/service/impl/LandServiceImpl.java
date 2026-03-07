package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.Land;
import com.clj.service.LandService;
import com.clj.mapper.LandMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【land(地块信息表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:21
*/
@Service
public class LandServiceImpl extends ServiceImpl<LandMapper, Land>
    implements LandService{

}




