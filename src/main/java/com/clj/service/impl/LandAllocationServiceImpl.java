package com.clj.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.constants.LandConstants;
import com.clj.domain.*;
import com.clj.domain.dto.LandAllocationDto;
import com.clj.domain.vo.ContractorInfoVo;
import com.clj.domain.vo.LandAllocationVo;
import com.clj.domain.vo.PlantingRecordVo;
import com.clj.service.LandAllocationService;
import com.clj.mapper.LandAllocationMapper;
import com.clj.service.LandService;
import com.clj.service.UserService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;

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
//    @Transactional
    public Result addLandAllocation(LandAllocationDto landAllocationDto) {
        LandAllocation landAllocation = new LandAllocation();
        BeanUtils.copyProperties(landAllocationDto, landAllocation);
        User one = userService.lambdaQuery().eq(User::getName, landAllocationDto.getContractorName())
                .eq(User::getPhone, landAllocationDto.getPhone())
                .eq(User::getRole,"user")
                .one();
        if (one == null){
            return Result.error("该承包人不存在");
        }
        landAllocation.setContractorId(one.getUserId());
        return this.saveOrUpdate(landAllocation) ?
                Result.ok() : Result.error("添加失败");
    }

    @Override
//    @Transactional  //删除分配记录的同时将地块状态改为未分配
    public Result deletelandAllocation(Long allocationId) {
        LandAllocation one = this.lambdaQuery().eq(LandAllocation::getAllocationId, allocationId).one();
        if (one == null){
            return Result.error("该分配记录不存在");
        }
        Long landId = one.getLandId();
        return this.removeById(allocationId) ?
                Result.ok() : Result.error("删除失败");
    }

    @Override
    public Result updateLandAllocation(LandAllocationDto dto) {
        // 根据承包人姓名和电话查询用户
        User user = userService.lambdaQuery()
                .eq(User::getName, dto.getContractorName())
                .eq(User::getPhone, dto.getPhone())
                .one();
        
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 获取用户 ID
        Long contractorId = user.getUserId();
        
        // 查询原有的地块分配记录
        LandAllocation allocation = this.getById(dto.getAllocationId());
        if (allocation == null) {
            return Result.error("地块分配记录不存在");
        }
        
        // 创建 LandAllocation 对象并进行属性复制
        LandAllocation landAllocation = new LandAllocation();
        BeanUtils.copyProperties(allocation, landAllocation);
        
        // 设置承包人 ID 和日期
        landAllocation.setContractorId(contractorId);
        landAllocation.setStartDate(dto.getStartDate());
        landAllocation.setEndDate(dto.getEndDate());
        
        // 更新地块分配记录
        return this.updateById(landAllocation) ? Result.ok() : Result.error("修改失败");
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

    @Override
    public Result getLandAllocationByPage(Integer pageNum, Integer pageSize) {
        // 分页查询地块分配记录
        Page<LandAllocation> page = this.page(new Page<>(pageNum, pageSize), 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<LandAllocation>()
                        .orderByDesc(LandAllocation::getCreateTime));
        
        // 收集所有的地块 ID 和承包人 ID
        ArrayList<Long> landIds = new ArrayList<>();
        ArrayList<Long> contractorIds = new ArrayList<>();
        for (LandAllocation allocation : page.getRecords()) {
            if (allocation.getLandId() != null) {
                landIds.add(allocation.getLandId());
            }
            if (allocation.getContractorId() != null) {
                contractorIds.add(allocation.getContractorId());
            }
        }

        // 批量查询地块信息
        ArrayList<Land> lands = new ArrayList<>();
        if (!landIds.isEmpty()) {
            lands = (ArrayList<Land>) landService.listByIds(landIds);
        }

        // 批量查询承包人信息
        ArrayList<User> contractors = new ArrayList<>();
        if (!contractorIds.isEmpty()) {
            contractors = (ArrayList<User>) userService.listByIds(contractorIds);
        }

        // 转换为 Map 便于快速查找
        java.util.Map<Long, Land> landMap = lands.stream()
                .collect(java.util.stream.Collectors.toMap(Land::getLandId, land -> land));
        java.util.Map<Long, User> contractorMap = contractors.stream()
                .collect(java.util.stream.Collectors.toMap(User::getUserId, user -> user));

        // 构建 VO 对象
        ArrayList<LandAllocationVo> landAllocationVos = new ArrayList<>();
        for (LandAllocation allocation : page.getRecords()) {
            LandAllocationVo landAllocationVo = new LandAllocationVo();
            
            // 设置分配 ID
            landAllocationVo.setAllocationId(allocation.getAllocationId());
            
            // 从 Map 中获取地块信息
            Land land = landMap.get(allocation.getLandId());
            if (land != null) {
                landAllocationVo.setLandName(land.getLandName());
                landAllocationVo.setArea(land.getArea().toString());
            }

            // 从 Map 中获取承包人信息
            User contractor = contractorMap.get(allocation.getContractorId());
            if (contractor != null) {
                landAllocationVo.setContractorName(contractor.getName());
                landAllocationVo.setPhone(contractor.getPhone());
            }

            // 设置分配日期
            landAllocationVo.setStartDate(allocation.getStartDate());
            landAllocationVo.setEndDate(allocation.getEndDate());

            landAllocationVos.add(landAllocationVo);
        }

        Page<LandAllocationVo> landAllocationVoPage = new Page<>(pageNum, pageSize, page.getTotal());
        landAllocationVoPage.setRecords(landAllocationVos);
        return Result.ok(landAllocationVoPage);
    }
    @Override
    public Result searchLandAllocationByPage(String keyword, Integer pageNum, Integer pageSize) {
        if(keyword==null || keyword.trim().isEmpty()){
            return this.getLandAllocationByPage(pageNum, pageSize);
        }
        
        // 分页查询地块分配记录，根据地号名称和承包人名称进行模糊查询
        Page<LandAllocation> page = this.page(new Page<>(pageNum, pageSize), 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<LandAllocation>()
                        .orderByDesc(LandAllocation::getCreateTime));
        
        // 收集所有的地块 ID 和承包人 ID
        ArrayList<Long> landIds = new ArrayList<>();
        ArrayList<Long> contractorIds = new ArrayList<>();
        for (LandAllocation allocation : page.getRecords()) {
            if (allocation.getLandId() != null) {
                landIds.add(allocation.getLandId());
            }
            if (allocation.getContractorId() != null) {
                contractorIds.add(allocation.getContractorId());
            }
        }

        // 批量查询地块信息
        ArrayList<Land> lands = new ArrayList<>();
        if (!landIds.isEmpty()) {
            lands = (ArrayList<Land>) landService.listByIds(landIds);
        }

        // 批量查询承包人信息
        ArrayList<User> contractors = new ArrayList<>();
        if (!contractorIds.isEmpty()) {
            contractors = (ArrayList<User>) userService.listByIds(contractorIds);
        }

        // 转换为 Map 便于快速查找
        java.util.Map<Long, Land> landMap = lands.stream()
                .collect(java.util.stream.Collectors.toMap(Land::getLandId, land -> land));
        java.util.Map<Long, User> contractorMap = contractors.stream()
                .collect(java.util.stream.Collectors.toMap(User::getUserId, user -> user));

        // 构建 VO 对象并过滤
        ArrayList<LandAllocationVo> landAllocationVos = new ArrayList<>();
        for (LandAllocation allocation : page.getRecords()) {
            LandAllocationVo landAllocationVo = new LandAllocationVo();
            
            // 设置分配 ID
            landAllocationVo.setAllocationId(allocation.getAllocationId());
            
            // 从 Map 中获取地块信息
            Land land = landMap.get(allocation.getLandId());
            if (land != null) {
                landAllocationVo.setLandName(land.getLandName());
                landAllocationVo.setArea(land.getArea().toString());
            }

            // 从 Map 中获取承包人信息
            User contractor = contractorMap.get(allocation.getContractorId());
            if (contractor != null) {
                landAllocationVo.setContractorName(contractor.getName());
                landAllocationVo.setPhone(contractor.getPhone());
            }

            // 设置分配日期
            landAllocationVo.setStartDate(allocation.getStartDate());
            landAllocationVo.setEndDate(allocation.getEndDate());
            
            // 根据地号名称或承包人名称进行过滤
            boolean matchLand = land != null && land.getLandName().contains(keyword);
            boolean matchContractor = contractor != null && contractor.getName().contains(keyword);
            
            if (matchLand || matchContractor) {
                landAllocationVos.add(landAllocationVo);
            }
        }

        // 手动分页
        int total = landAllocationVos.size();
        int fromIndex = Math.min((pageNum - 1) * pageSize, total);
        int toIndex = Math.min(fromIndex + pageSize, total);
        ArrayList<LandAllocationVo> pagedList = new ArrayList<>();
        if (fromIndex < total) {
            pagedList = new ArrayList<>(landAllocationVos.subList(fromIndex, toIndex));
        }

        Page<LandAllocationVo> landAllocationVoPage = new Page<>(pageNum, pageSize, total);
        landAllocationVoPage.setRecords(pagedList);
        return Result.ok(landAllocationVoPage);
    }

}




