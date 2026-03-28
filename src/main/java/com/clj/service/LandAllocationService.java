package com.clj.service;

import com.clj.domain.LandAllocation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.dto.LandAllocationDto;
import com.clj.utils.Result;

/**
* @author ajie
* @description 针对表【land_allocation(地块分配表)】的数据库操作 Service
* @createDate 2026-03-02 20:08:19
*/
public interface LandAllocationService extends IService<LandAllocation> {

    Result addLandAllocation(LandAllocationDto landAllocationDto);

    Result deletelandAllocation(Long landAllocationId);

    Result updateLandAllocation(LandAllocationDto dto);

    Result getLandAllocationByPage(Integer pageNum, Integer pageSize);

    Result getContractorInfoByLandId(Long landId);

    Result searchLandAllocationByPage(String keyword, Integer pageNum, Integer pageSize);
}
