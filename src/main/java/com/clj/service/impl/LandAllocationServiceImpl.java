package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.LandAllocation;
import com.clj.service.LandAllocationService;
import com.clj.mapper.LandAllocationMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【land_allocation(地块分配表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:19
*/
@Service
public class LandAllocationServiceImpl extends ServiceImpl<LandAllocationMapper, LandAllocation>
    implements LandAllocationService{

}




