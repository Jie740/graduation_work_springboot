package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.constants.PlantingPlanConstants;
import com.clj.domain.Crop;
import com.clj.domain.Land;
import com.clj.domain.PlantingPlan;
import com.clj.domain.User;
import com.clj.domain.dto.PlantingPlanDto;
import com.clj.domain.vo.PlantingPlanVo;
import com.clj.mapper.PlantingPlanMapper;
import com.clj.service.CropService;
import com.clj.service.LandService;
import com.clj.service.PlantingPlanService;
import com.clj.service.UserService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    @Override
    public Result add(PlantingPlanDto plantingPlanDto) {
        //通过地块名和地块位置查询地块ID
        Land land = landService.lambdaQuery().eq(Land::getLandName, plantingPlanDto.getLandName())
                .eq(Land::getLocation, plantingPlanDto.getLandLocation())
                .one();
        if (land == null){
            return Result.error("地块不存在");
        }
        Long landId = land.getLandId();
        //通过农作物名查询农作物ID
        Crop crop = cropService.lambdaQuery().eq(Crop::getCropName, plantingPlanDto.getCropName())
                .one();
        if (crop == null){
            return Result.error("农作物不存在");
        }
        Long cropId = crop.getCropId();

        PlantingPlan plantingPlan = new PlantingPlan();
        plantingPlan.setLandId(landId);
        plantingPlan.setCropId(cropId);

        BeanUtils.copyProperties(plantingPlanDto, plantingPlan);

        return this.save(plantingPlan)?Result.ok():Result.error("添加失败");
    }

    @Override
    public Result delete(Long id) {
        return this.removeById(id)?Result.ok():Result.error("删除失败");
    }

    @Override
    public Result updatePlantingPlan(PlantingPlan plantingPlan) {
        return this.updateById(plantingPlan)?Result.ok():Result.error("修改失败");
    }

    @Override
    public Result getPlantingPlansByPage(Integer pageNum, Integer pageSize) {
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

        return Result.ok(voPage);
    }

//    查询条件：计划名
    @Override
    public Result searchPlantingPlansByPage(String keyword, Integer pageNum, Integer pageSize) {
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

        return Result.ok(voPage);
    }

    @Override
    public Result updateStatus(Long planId, Integer status) {
        return this.lambdaUpdate().eq(PlantingPlan::getPlanId, planId)
                .set(PlantingPlan::getStatus, status)
                .update()?Result.ok():Result.error("修改失败");
    }

    @Override
    public Result getPlantingPlanById(Long planId) {
        PlantingPlan plantingPlan = this.getById(planId);
        if (plantingPlan == null){
            return Result.error("种植计划不存在");
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
        return Result.ok(vo);
    }

    @Override
    public Result getPublishedPlantingPlans() {
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
        return Result.ok(voList);
    }

    @Override
    public Result getByLandId(Long landId) {
        //获取正在执行的计划
        PlantingPlan plantingPlan = this.lambdaQuery().eq(PlantingPlan::getLandId, landId)
                .eq(PlantingPlan::getStatus, PlantingPlanConstants.PUBLISH).one();
        return plantingPlan == null?Result.error("该地块没有种植计划"):Result.ok(plantingPlan);
    }
}




