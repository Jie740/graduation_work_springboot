package com.clj.service;

import com.clj.domain.LandAllocation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.utils.Result;

/**
* @author ajie
* @description 针对表【land_allocation(地块分配表)】的数据库操作Service
* @createDate 2026-03-02 20:08:19
*/
public interface LandAllocationService extends IService<LandAllocation> {

    Result addLandAllocation(LandAllocation landAllocation);

    Result deletelandAllocation(Long landAllocationId);

    Result updateLandAllocation(LandAllocation landAllocation);

    Result getLandAllocationByPage(Integer pageNum, Integer pageSize);

    Result getContractorInfoByLandId(Long landId);

}
