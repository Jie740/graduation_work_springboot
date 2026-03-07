package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.PlantingPlan;
import com.clj.service.PlantingPlanService;
import com.clj.mapper.PlantingPlanMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【planting_plan(种植计划表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:04
*/
@Service
public class PlantingPlanServiceImpl extends ServiceImpl<PlantingPlanMapper, PlantingPlan>
    implements PlantingPlanService{

}




