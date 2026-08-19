package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.common.constant.PlantingPlanConstants;
import com.clj.common.exception.BusinessException;
import com.clj.domain.Crop;
import com.clj.domain.Land;
import com.clj.domain.LandAllocation;
import com.clj.domain.PlantingPlan;
import com.clj.domain.User;
import com.clj.domain.dto.PlantingPlanDto;
import com.clj.domain.vo.PlantingPlanVo;
import com.clj.mapper.PlantingPlanMapper;
import com.clj.security.util.SecurityUtil;
import com.clj.service.CropService;
import com.clj.service.LandAllocationService;
import com.clj.service.LandService;
import com.clj.service.PlantingPlanService;
import com.clj.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author ajie
* @description 针对表【planting_plan(种植计划表)】的数据库操作 Service 实现
* @createDate 2026-03-02 20:08:04
*/
@Service
@RequiredArgsConstructor
public class PlantingPlanServiceImpl extends ServiceImpl<PlantingPlanMapper, PlantingPlan>
    implements PlantingPlanService{

    final UserService userService;
    final LandService landService;
    final CropService cropService;
    final LandAllocationService landAllocationService;

    @Override
    public void add(PlantingPlanDto plantingPlanDto) {
        //查询角色是否为企业管理员或系统管理员
        Long userId = SecurityUtil.getUserId();
        if (userId== null){
            throw new BusinessException("请先登录");
        }
        User user = userService.getById(userId);
        if (user == null || (!"ENTERPRISE_ADMIN".equals(user.getRole()) && !"SYSTEM_ADMIN".equals(user.getRole()))){
            throw new BusinessException("无权限");
        }
        //查询计划名是否存在
        PlantingPlan plantingPlan1 = this.lambdaQuery().eq(PlantingPlan::getPlanName, plantingPlanDto.getPlanName())
                .one();
        if (plantingPlan1 != null){
            throw new BusinessException("计划名已存在");
        }

        //通过地块名和地块位置查询地块ID
        Land land = landService.lambdaQuery().eq(Land::getLandName, plantingPlanDto.getLandName())
                .eq(Land::getLocation, plantingPlanDto.getLandLocation())
                .one();
        if (land == null){
            throw new BusinessException("地块不存在");
        }
        Long landId = land.getLandId();

        //查询地块是否有正在执行的计划
        PlantingPlan plantingPlan2 = this.lambdaQuery().eq(PlantingPlan::getLandId, landId)
                .eq(PlantingPlan::getStatus, 1)
                .one();
        if (plantingPlan2 != null){
            throw new BusinessException("该地块有正在执行的计划");
        }

        //通过农作物名查询农作物ID
        Crop crop = cropService.lambdaQuery().eq(Crop::getCropName, plantingPlanDto.getCropName())
                .one();
        if (crop == null){
            throw new BusinessException("农作物不存在");
        }
        Long cropId = crop.getCropId();

        PlantingPlan plantingPlan = new PlantingPlan();
        plantingPlan.setCreatorId(userId);
        plantingPlan.setLandId(landId);
        plantingPlan.setCropId(cropId);

        BeanUtils.copyProperties(plantingPlanDto, plantingPlan);

        if (!this.save(plantingPlan)) {
            throw new BusinessException("添加失败");
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!this.removeById(id)) {
            throw new BusinessException("删除失败");
        }
    }

    @Override
    public void updatePlantingPlan(PlantingPlan plantingPlan) {
        PlantingPlan plantingPlan1 = this.lambdaQuery().eq(PlantingPlan::getPlanId, plantingPlan.getPlanId()).one();
        Integer oldStatus = plantingPlan1.getStatus();
        Integer newStatus = plantingPlan.getStatus();
        if (oldStatus!=1){
            if (newStatus==1){
                //先查询对应地块是否有正在执行的计划 如果有 则提示不能发布 已有计划
                Long landId = plantingPlan1.getLandId();
                boolean exists = this.lambdaQuery().eq(PlantingPlan::getLandId, landId)
                        .eq(PlantingPlan::getStatus, 1)
                        .exists();
                if (exists){
                    throw new BusinessException("该地块已有计划正在执行");
                }
            }
        }
        if (!this.updateById(plantingPlan)) {
            throw new BusinessException("修改失败");
        }
    }

    @Override
    public Page<PlantingPlanVo> getPlantingPlansByPage(Integer pageNum, Integer pageSize) {
        // 分页查询种植计划
        Page<PlantingPlan> page = new Page<>(pageNum, pageSize);
        Page<PlantingPlan> plantingPlanPage = this.lambdaQuery().page(page);

        // 转换为 VO 列表
        List<PlantingPlanVo> voList = new ArrayList<>();
        for (PlantingPlan plantingPlan : plantingPlanPage.getRecords()) {
            PlantingPlanVo vo = new PlantingPlanVo();
            // 复制基本属性
            BeanUtils.copyProperties(plantingPlan, vo);

            // 根据 landId 查询地块信息
            if (plantingPlan.getLandId() != null) {
                Land land = landService.getById(plantingPlan.getLandId());
                if (land != null) {
                    vo.setLandName(land.getLandName());
                    vo.setLandLocation(land.getLocation());
                    vo.setLandArea(land.getArea());
                }
            }

            // 根据 cropId 查询农作物信息
            if (plantingPlan.getCropId() != null) {
                Crop crop = cropService.getById(plantingPlan.getCropId());
                if (crop != null) {
                    vo.setCropName(crop.getCropName());
                }
            }

            // 根据 creatorId 查询创建人信息
            if (plantingPlan.getCreatorId() != null) {
                User user = userService.getById(plantingPlan.getCreatorId());
                if (user != null) {
                    vo.setCreator(user.getName());
                }
            }

            voList.add(vo);
        }

        // 创建新的分页对象，包含转换后的 VO 数据
        Page<PlantingPlanVo> voPage = new Page<>(pageNum, pageSize, plantingPlanPage.getTotal());
        voPage.setRecords(voList);

        return voPage;
    }

//    查询条件：计划名
    @Override
    public Page<PlantingPlanVo> searchPlantingPlansByPage(String keyword, Integer pageNum, Integer pageSize) {
        if (keyword == null){
            return getPlantingPlansByPage(pageNum, pageSize);
        }
        // 分页查询种植计划
        Page<PlantingPlan> page = new Page<>(pageNum, pageSize);
        Page<PlantingPlan> plantingPlanPage = this.lambdaQuery().likeRight(PlantingPlan::getPlanName, keyword)
                .page(page);

        // 转换为 VO 列表
        List<PlantingPlanVo> voList = new ArrayList<>();
        for (PlantingPlan plantingPlan : plantingPlanPage.getRecords()) {
            PlantingPlanVo vo = new PlantingPlanVo();
            // 复制基本属性
            BeanUtils.copyProperties(plantingPlan, vo);

            // 根据 landId 查询地块信息
            if (plantingPlan.getLandId() != null) {
                Land land = landService.getById(plantingPlan.getLandId());
                if (land != null) {
                    vo.setLandName(land.getLandName());
                    vo.setLandLocation(land.getLocation());
                    vo.setLandArea(land.getArea());
                }
            }

            // 根据 cropId 查询农作物信息
            if (plantingPlan.getCropId() != null) {
                Crop crop = cropService.getById(plantingPlan.getCropId());
                if (crop != null) {
                    vo.setCropName(crop.getCropName());
                }
            }

            // 根据 creatorId 查询创建人信息
            if (plantingPlan.getCreatorId() != null) {
                User user = userService.getById(plantingPlan.getCreatorId());
                if (user != null) {
                    vo.setCreator(user.getName());
                }
            }

            voList.add(vo);
        }

        // 创建新的分页对象，包含转换后的 VO 数据
        Page<PlantingPlanVo> voPage = new Page<>(pageNum, pageSize, plantingPlanPage.getTotal());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public void updateStatus(Long planId, Integer status) {
        //发布计划
        //先获取当前计划状态
        Integer oldStatus = this.lambdaQuery().eq(PlantingPlan::getPlanId, planId)
                .one().getStatus();
        if (oldStatus!=1){
            if (status==1){
                //先查询对应地块是否有正在执行的计划 如果有 则提示不能发布 已有计划
                PlantingPlan plantingplan = this.lambdaQuery().eq(PlantingPlan::getPlanId, planId).one();
                Long landId = plantingplan.getLandId();
                boolean exists = this.lambdaQuery().eq(PlantingPlan::getLandId, landId)
                        .eq(PlantingPlan::getStatus, 1)
                        .exists();
                if (exists){
                    throw new BusinessException("该地块已有计划正在执行");
                }
            }
        }

        if (!this.lambdaUpdate().eq(PlantingPlan::getPlanId, planId)
                .set(PlantingPlan::getStatus, status)
                .update()) {
            throw new BusinessException("修改失败");
        }
    }

    @Override
    public PlantingPlanVo getPlantingPlanById(Long planId) {
        PlantingPlan plantingPlan = this.getById(planId);
        if (plantingPlan == null){
            throw new BusinessException("种植计划不存在");
        }
        PlantingPlanVo vo = new PlantingPlanVo();
        // 复制基本属性
        BeanUtils.copyProperties(plantingPlan, vo);

        // 根据 landId 查询地块信息
        if (plantingPlan.getLandId() != null) {
            Land land = landService.getById(plantingPlan.getLandId());
            if (land != null) {
                vo.setLandName(land.getLandName());
                vo.setLandLocation(land.getLocation());
                vo.setLandArea(land.getArea());
            }
        }

        // 根据 cropId 查询农作物信息
        if (plantingPlan.getCropId() != null) {
            Crop crop = cropService.getById(plantingPlan.getCropId());
            if (crop != null) {
                vo.setCropName(crop.getCropName());
            }
        }

        // 根据 creatorId 查询创建人信息
        if (plantingPlan.getCreatorId() != null) {
            User user = userService.getById(plantingPlan.getCreatorId());
            if (user != null) {
                vo.setCreator(user.getName());
            }
        }
        return vo;
    }

    @Override
    public List<PlantingPlanVo> getPublishedPlantingPlans() {
        List<PlantingPlan> list = this.lambdaQuery().eq(PlantingPlan::getStatus, PlantingPlanConstants.PUBLISH).list();
        List<PlantingPlanVo> voList = new ArrayList<>();
        for (PlantingPlan plantingPlan : list) {
            PlantingPlanVo vo = new PlantingPlanVo();
            // 复制基本属性
            BeanUtils.copyProperties(plantingPlan, vo);

            // 根据 landId 查询地块信息
            if (plantingPlan.getLandId() != null) {
                Land land = landService.getById(plantingPlan.getLandId());
                if (land != null) {
                    vo.setLandName(land.getLandName());
                    vo.setLandLocation(land.getLocation());
                    vo.setLandArea(land.getArea());
                }
            }

            // 根据 cropId 查询农作物信息
            if (plantingPlan.getCropId() != null) {
                Crop crop = cropService.getById(plantingPlan.getCropId());
                if (crop != null) {
                    vo.setCropName(crop.getCropName());
                }
            }

            // 根据 creatorId 查询创建人信息
            if (plantingPlan.getCreatorId() != null) {
                User user = userService.getById(plantingPlan.getCreatorId());
                if (user != null) {
                    vo.setCreator(user.getName());
                }
            }

            voList.add(vo);
        }
        return voList;
    }

    @Override
    public PlantingPlan getByLandId(Long landId) {
        //获取正在执行的计划
        PlantingPlan plantingPlan = this.lambdaQuery().eq(PlantingPlan::getLandId, landId)
                .eq(PlantingPlan::getStatus, PlantingPlanConstants.PUBLISH).one();
        if (plantingPlan == null){
            throw new BusinessException("该地块没有种植计划");
        }
        return plantingPlan;
    }

    @Override
    public List<PlantingPlanVo> getMyPlans() {
        Long userId = SecurityUtil.getUserId();
        if (userId == null){
            throw new BusinessException("请先登录");
        }

        // 1. 根据用户ID查询地块分配表，获取该用户分配的地块ID列表
        List<LandAllocation> allocations = landAllocationService.lambdaQuery()
                .eq(LandAllocation::getContractorId, userId)
                .list();

        // 如果没有分配的地块，返回空列表
        if (allocations.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 收集所有地块ID
        ArrayList<Long> landIds = new ArrayList<>();
        for (LandAllocation allocation : allocations) {
            if (allocation.getLandId() != null) {
                landIds.add(allocation.getLandId());
            }
        }

        // 3. 根据地块ID列表查询种植计划
        List<PlantingPlan> plantingPlans = new ArrayList<>();
        if (!landIds.isEmpty()) {
            plantingPlans = this.lambdaQuery()
                    .in(PlantingPlan::getLandId, landIds)
                    .list();
        }

        // 4. 转换为 VO 列表
        List<PlantingPlanVo> voList = new ArrayList<>();
        for (PlantingPlan plantingPlan : plantingPlans) {
            PlantingPlanVo vo = new PlantingPlanVo();
            BeanUtils.copyProperties(plantingPlan, vo);

            // 根据 landId 查询地块信息
            if (plantingPlan.getLandId() != null) {
                Land land = landService.getById(plantingPlan.getLandId());
                if (land != null) {
                    vo.setLandName(land.getLandName());
                    vo.setLandLocation(land.getLocation());
                    vo.setLandArea(land.getArea());
                }
            }

            // 根据 cropId 查询农作物信息
            if (plantingPlan.getCropId() != null) {
                Crop crop = cropService.getById(plantingPlan.getCropId());
                if (crop != null) {
                    vo.setCropName(crop.getCropName());
                }
            }

            // 根据 creatorId 查询创建人信息
            if (plantingPlan.getCreatorId() != null) {
                User user = userService.getById(plantingPlan.getCreatorId());
                if (user != null) {
                    vo.setCreator(user.getName());
                }
            }

            voList.add(vo);
        }

        return voList;
    }

    @Override
    public List<PlantingPlanVo> getPublishedPlantingPlanByUserId() {
        // 1. 从 SecurityContext 获取当前用户ID
        Long userId = SecurityUtil.getUserId();
        if (userId == null) {
            throw new BusinessException("未登录或登录已过期");
        }

        // 2. 根据用户ID查询地块分配表，获取该用户分配的地块ID列表
        List<LandAllocation> allocations = landAllocationService.lambdaQuery()
                .eq(LandAllocation::getContractorId, userId)
                .list();

        // 如果没有分配的地块，返回空列表
        if (allocations.isEmpty()) {
            return new ArrayList<>();
        }

        // 3. 收集所有地块ID
        List<Long> landIds = allocations.stream()
                .map(LandAllocation::getLandId)
                .filter(landId -> landId != null)
                .collect(Collectors.toList());

        // 4. 根据地块ID列表查询正在执行（status=1）的种植计划
        List<PlantingPlan> list = new ArrayList<>();
        if (!landIds.isEmpty()) {
            list = this.lambdaQuery()
                    .in(PlantingPlan::getLandId, landIds)
                    .eq(PlantingPlan::getStatus, PlantingPlanConstants.PUBLISH)
                    .list();
        }

        // 5. 转换为 VO 列表
        List<PlantingPlanVo> voList = new ArrayList<>();
        for (PlantingPlan plantingPlan : list) {
            PlantingPlanVo vo = new PlantingPlanVo();
            // 复制基本属性
            BeanUtils.copyProperties(plantingPlan, vo);

            // 根据 landId 查询地块信息
            if (plantingPlan.getLandId() != null) {
                Land land = landService.getById(plantingPlan.getLandId());
                if (land != null) {
                    vo.setLandName(land.getLandName());
                    vo.setLandLocation(land.getLocation());
                    vo.setLandArea(land.getArea());
                }
            }

            // 根据 cropId 查询农作物信息
            if (plantingPlan.getCropId() != null) {
                Crop crop = cropService.getById(plantingPlan.getCropId());
                if (crop != null) {
                    vo.setCropName(crop.getCropName());
                }
            }

            // 根据 creatorId 查询创建人信息
            if (plantingPlan.getCreatorId() != null) {
                User user = userService.getById(plantingPlan.getCreatorId());
                if (user != null) {
                    vo.setCreator(user.getName());
                }
            }

            voList.add(vo);
        }
        return voList;
    }

    @Override
    public Map<String, String> getUserNameByPlanId(Long planId) {
        PlantingPlan plantingPlan = this.lambdaQuery().eq(PlantingPlan::getPlanId, planId)
                .one();
        Long creatorId = plantingPlan.getCreatorId();
        User user = userService.lambdaQuery().eq(User::getUserId, creatorId)
                .one();
        HashMap<String, String> map = new HashMap<>();
        map.put("creator", user.getName());
        return map;
    }
}
