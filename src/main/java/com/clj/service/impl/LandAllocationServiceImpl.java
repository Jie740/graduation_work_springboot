package com.clj.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.constants.LandConstants;
import com.clj.domain.*;
import com.clj.domain.dto.LandAllocationDto;
import com.clj.domain.vo.ContractorInfoVo;
import com.clj.domain.vo.ContractorLandVo;
import com.clj.domain.vo.LandAllocationVo;
import com.clj.domain.vo.PlantingRecordVo;
import com.clj.service.LandAllocationService;
import com.clj.mapper.LandAllocationMapper;
import com.clj.service.LandService;
import com.clj.service.UserService;
import com.clj.utils.Result;
import com.clj.utils.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

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
        
        // 查询该地块在相同时间范围内是否已有分配记录
        // 时间冲突判断：已存在记录的起止时间与新增记录的起止时间有重叠
        Long landId = landAllocation.getLandId();
        java.util.Date newStartDate = landAllocation.getStartDate();
        java.util.Date newEndDate = landAllocation.getEndDate();
        
        // 查询条件：地块ID相同，且时间区间有重叠
        // 时间重叠条件：existing.startDate <= new.endDate AND existing.endDate >= new.startDate
        long count = this.lambdaQuery()
                .eq(LandAllocation::getLandId, landId)
                .le(LandAllocation::getStartDate, newEndDate)
                .ge(LandAllocation::getEndDate, newStartDate)
                .count();
        
        if (count > 0) {
            return Result.error("该地块在该时间段内已被分配");
        }
        
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

    @Override
    public Result getMyLands() {
        // 1. 从 ThreadLocal 获取当前用户ID
        Long userId = UserHolder.getUserId();
        if (userId == null) {
            return Result.error("未登录或登录已过期");
        }

        // 2. 根据用户ID查询地块分配表，获取该用户分配的所有地块ID
        List<LandAllocation> allocations = this.lambdaQuery()
                .eq(LandAllocation::getContractorId, userId)
                .list();

        // 如果没有分配的地块，返回空列表
        if (allocations.isEmpty()) {
            return Result.ok(new ArrayList<>());
        }

        // 3. 收集所有地块ID
        ArrayList<Long> landIds = new ArrayList<>();
        for (LandAllocation allocation : allocations) {
            if (allocation.getLandId() != null) {
                landIds.add(allocation.getLandId());
            }
        }

        // 4. 批量查询地块信息
        List<Land> lands = new ArrayList<>();
        if (!landIds.isEmpty()) {
            lands = landService.listByIds(landIds);
        }

        // 5. 构建地块Map便于查找
        java.util.Map<Long, Land> landMap = lands.stream()
                .collect(java.util.stream.Collectors.toMap(Land::getLandId, land -> land));

        // 6. 根据地块ID去重，只保留最新的分配记录
        java.util.Map<Long, LandAllocation> latestAllocationMap = new java.util.HashMap<>();
        for (LandAllocation allocation : allocations) {
            Long landId = allocation.getLandId();
            if (landId != null) {
                // 如果该地块还没有记录，或者当前记录的结束时间更晚，则更新
                LandAllocation existing = latestAllocationMap.get(landId);
                if (existing == null || 
                    (allocation.getEndDate() != null && existing.getEndDate() != null && 
                     allocation.getEndDate().after(existing.getEndDate()))) {
                    latestAllocationMap.put(landId, allocation);
                }
            }
        }

        // 7. 构建 ContractorLandVo 列表
        ArrayList<ContractorLandVo> voList = new ArrayList<>();
        for (LandAllocation allocation : latestAllocationMap.values()) {
            Land land = landMap.get(allocation.getLandId());
            if (land != null) {
                ContractorLandVo vo = new ContractorLandVo();
                // 设置地块信息
                vo.setLandId(land.getLandId());
                vo.setLandName(land.getLandName());
                vo.setLocation(land.getLocation());
                vo.setArea(land.getArea());
                vo.setSoilType(land.getSoilType());
                // 设置分配日期
                vo.setStartDate(allocation.getStartDate());
                vo.setEndDate(allocation.getEndDate());
                voList.add(vo);
            }
        }

        return Result.ok(voList);
    }

}




