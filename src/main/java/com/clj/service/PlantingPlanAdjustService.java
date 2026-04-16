package com.clj.service;

import com.clj.domain.PlantingPlanAdjust;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.dto.PlantingPlanAdjustDto;
import com.clj.utils.Result;

/**
* @author ajie
* @description 针对表【planting_plan_adjust(种植计划调整申请表)】的数据库操作Service
* @createDate 2026-03-02 20:08:02
*/
public interface PlantingPlanAdjustService extends IService<PlantingPlanAdjust> {

    Result add(PlantingPlanAdjustDto plantingPlanAdjustDto);
    Result delete(Long id);
    Result updateStatus(Long id, Integer status);
    Result getPlantingPlanAdjustsByPage(Integer pageNum, Integer pageSize);

    Result searchPlantingPlanAdjustsByPage(String keyword, Integer pageNum, Integer pageSize);

    Result updatePlantingPlanAdjust(PlantingPlanAdjustDto plantingPlanAdjustDto);

    Result getPlantingPlanAdjustsByAdjustId(Long adjustId);

    Result getPlantingPlanAdjustsByUserIdPage(Integer pageNum, Integer pageSize);

    Result getPlantingPlanAdjustsByUser(String keyword, Integer pageNum, Integer pageSize);

    Result cancel(Long adjustId);
}
