package com.clj.service;

import com.clj.domain.PlantingPlanAdjust;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.dto.PlantingPlanAdjustDto;
import com.clj.domain.vo.PlantingPlanAdjustDetailVo;
import com.clj.domain.vo.PlantingPlanAdjustVo;

/**
* @author ajie
* @description 针对表【planting_plan_adjust(种植计划调整申请表)】的数据库操作Service
* @createDate 2026-03-02 20:08:02
*/
public interface PlantingPlanAdjustService extends IService<PlantingPlanAdjust> {

    void add(PlantingPlanAdjustDto plantingPlanAdjustDto);
    void delete(Long id);
    void updateStatus(Long id, Integer status);
    Page<PlantingPlanAdjustVo> getPlantingPlanAdjustsByPage(Integer pageNum, Integer pageSize);

    Page<PlantingPlanAdjustVo> searchPlantingPlanAdjustsByPage(String keyword, Integer pageNum, Integer pageSize);

    void updatePlantingPlanAdjust(PlantingPlanAdjustDto plantingPlanAdjustDto);

    PlantingPlanAdjustDetailVo getPlantingPlanAdjustsByAdjustId(Long adjustId);

    Page<PlantingPlanAdjustVo> getPlantingPlanAdjustsByUserIdPage(Integer pageNum, Integer pageSize);

    Page<PlantingPlanAdjustVo> getPlantingPlanAdjustsByUser(String keyword, Integer pageNum, Integer pageSize);

    void cancel(Long adjustId);
}
