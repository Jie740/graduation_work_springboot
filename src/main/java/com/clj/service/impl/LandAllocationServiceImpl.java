package com.clj.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.constants.LandConstants;
import com.clj.domain.LandAllocation;
import com.clj.domain.User;
import com.clj.domain.vo.ContractorInfoVo;
import com.clj.service.LandAllocationService;
import com.clj.mapper.LandAllocationMapper;
import com.clj.service.LandService;
import com.clj.service.UserService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import static com.clj.constants.LandConstants.ALLOCATED;
import static com.clj.constants.LandConstants.UN_ALLOCATED;

/**
* @author ajie
* @description 针对表【land_allocation(地块分配表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:19
*/
@Service
@RequiredArgsConstructor
public class LandAllocationServiceImpl extends ServiceImpl<LandAllocationMapper, LandAllocation>
    implements LandAllocationService{
    final UserService userService;
    final LandService landService;

    @Override
    @Transactional
    public Result addLandAllocation(LandAllocation landAllocation) {
        System.out.println(landAllocation);
        boolean save = this.save(landAllocation);
        if(save){
            save=landService.updateLandStatus(landAllocation.getLandId(), ALLOCATED);
        }
        return save?Result.ok():Result.error("添加失败");
    }

    @Override
    @Transactional  //删除分配记录的同时将地块状态改为未分配
    public Result deletelandAllocation(Long allocationId) {
        LandAllocation one = this.lambdaQuery().eq(LandAllocation::getAllocationId, allocationId).one();
        if (one == null){
            return Result.error("该分配记录不存在");
        }
        Long landId = one.getLandId();
        return this.removeById(allocationId) && landService.updateLandStatus(landId, UN_ALLOCATED) ?
                Result.ok() : Result.error("删除失败");
    }

    @Override
    public Result updateLandAllocation(LandAllocation landAllocation) {
        return this.updateById(landAllocation)?Result.ok():Result.error("修改失败");
    }

    @Override
    public Result getLandAllocationByPage(Integer pageNum, Integer pageSize) {
        Page<LandAllocation> page = new Page<>(pageNum, pageSize);
        return Result.ok(this.lambdaQuery().page(page));
    }

    @Override
    public Result getContractorInfoByLandId(Long landId) {
        LandAllocation allocationRecord = this.lambdaQuery().eq(LandAllocation::getLandId, landId)
                .one();
        Long contractorId = allocationRecord.getContractorId();
        User user = userService.getById(contractorId);
        ContractorInfoVo contractorInfoVo = BeanUtil.copyProperties(user, ContractorInfoVo.class);
        contractorInfoVo.setAllocationId(allocationRecord.getAllocationId());
        contractorInfoVo.setStartDate(allocationRecord.getStartDate());
        contractorInfoVo.setEndDate(allocationRecord.getEndDate());
        return Result.ok(contractorInfoVo);
    }

}




