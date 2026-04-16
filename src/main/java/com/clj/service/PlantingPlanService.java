package com.clj.service;

import com.clj.domain.PlantingPlan;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.dto.PlantingPlanDto;
import com.clj.utils.Result;

/**
* @author ajie
* @description 针对表【planting_plan(种植计划表)】的数据库操作Service
* @createDate 2026-03-02 20:08:04
*/
public interface PlantingPlanService extends IService<PlantingPlan> {
    Result add(PlantingPlanDto plantingPlanDto);
    Result delete(Long id);
    Result updatePlantingPlan(PlantingPlan plantingPlan);
    Result getPlantingPlansByPage(Integer pageNum, Integer pageSize);
    Result searchPlantingPlansByPage(String keyword, Integer pageNum, Integer pageSize);

    Result updateStatus(Long planId, Integer status);

    Result getPlantingPlanById(Long planId);

    Result getPublishedPlantingPlans();

    Result getByLandId(Long landId);

    Result getMyPlans();

    Result getPublishedPlantingPlanByUserId();

    Result getUserNameByPlanId(Long planId);
}
