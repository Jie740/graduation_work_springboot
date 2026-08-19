package com.clj.service;

import com.clj.domain.LandAllocation;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.dto.LandAllocationDto;
import com.clj.domain.vo.ContractorInfoVo;
import com.clj.domain.vo.ContractorLandVo;
import com.clj.domain.vo.LandAllocationVo;

import java.util.List;

/**
* @author ajie
* @description 针对表【land_allocation(地块分配表)】的数据库操作 Service
* @createDate 2026-03-02 20:08:19
*/
public interface LandAllocationService extends IService<LandAllocation> {

    void addLandAllocation(LandAllocationDto landAllocationDto);

    void deletelandAllocation(Long landAllocationId);

    void updateLandAllocation(LandAllocationDto dto);

    Page<LandAllocationVo> getLandAllocationByPage(Integer pageNum, Integer pageSize);

    ContractorInfoVo getContractorInfoByLandId(Long landId);

    Page<LandAllocationVo> searchLandAllocationByPage(String keyword, Integer pageNum, Integer pageSize);

    List<ContractorLandVo> getMyLands();
}
