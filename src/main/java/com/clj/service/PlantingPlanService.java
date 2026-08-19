package com.clj.service;

import com.clj.domain.PlantingPlan;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.dto.PlantingPlanDto;
import com.clj.domain.vo.PlantingPlanVo;

import java.util.List;
import java.util.Map;

/**
* @author ajie
* @description 针对表【planting_plan(种植计划表)】的数据库操作Service
* @createDate 2026-03-02 20:08:04
*/
public interface PlantingPlanService extends IService<PlantingPlan> {
    void add(PlantingPlanDto plantingPlanDto);
    void delete(Long id);
    void updatePlantingPlan(PlantingPlan plantingPlan);
    Page<PlantingPlanVo> getPlantingPlansByPage(Integer pageNum, Integer pageSize);
    Page<PlantingPlanVo> searchPlantingPlansByPage(String keyword, Integer pageNum, Integer pageSize);

    void updateStatus(Long planId, Integer status);

    PlantingPlanVo getPlantingPlanById(Long planId);

    List<PlantingPlanVo> getPublishedPlantingPlans();

    PlantingPlan getByLandId(Long landId);

    List<PlantingPlanVo> getMyPlans();

    List<PlantingPlanVo> getPublishedPlantingPlanByUserId();

    Map<String, String> getUserNameByPlanId(Long planId);
}
