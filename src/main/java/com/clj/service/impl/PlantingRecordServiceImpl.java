package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.*;
import com.clj.domain.dto.PlantingRecordDto;
import com.clj.domain.vo.PlantingRecordVo;
import com.clj.service.*;
import com.clj.mapper.PlantingRecordMapper;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
* @author ajie
* @description 针对表【planting_record(地块种植记录表)】的数据库操作Service实现
* @createDate 2026-03-02 20:07:58
*/
@Service
@RequiredArgsConstructor
public class PlantingRecordServiceImpl extends ServiceImpl<PlantingRecordMapper, PlantingRecord>
    implements PlantingRecordService{

    final LandService landService;
    final CropService cropService;
    final MatureCropService matureCropService;
    final PlantingPlanService plantingPlanService;
    @Override
    public Result add(PlantingRecordDto plantingRecordDto) {
        PlantingRecord plantingRecord = new PlantingRecord();
        BeanUtils.copyProperties(plantingRecordDto,plantingRecord);
        return save(plantingRecord)?Result.ok():Result.error("添加失败");
    }

    @Override
    @Transactional
    public Result updatePlantingRecord(PlantingRecordDto plantingRecordDto) {
        //编辑为已成熟
        if (plantingRecordDto.getStatus()==1){
            //添加成熟作物表
            MatureCrop matureCrop = new MatureCrop();
            matureCrop.setRecordId(plantingRecordDto.getRecordId());
            matureCrop.setOutputQuantity(plantingRecordDto.getOutputQuantity());
            matureCrop.setHarvestTime(plantingRecordDto.getActualHarvestDate());
            matureCropService.add(matureCrop);

            //修改计划表状态为已完成
            System.out.println(plantingRecordDto.getPlanId());
            plantingPlanService.updateStatus(plantingRecordDto.getPlanId(),3);

        }
        PlantingRecord plantingRecord = new PlantingRecord();
        BeanUtils.copyProperties(plantingRecordDto,plantingRecord);
        return updateById(plantingRecord)?Result.ok():Result.error("更新失败");
    }

    @Override
    public Result getPlantingRecordsByPage(Integer pageNum, Integer pageSize) {
        Page<PlantingRecord> plantingRecordPage = new Page<>(pageNum, pageSize);
        Page<PlantingRecord> page = this.page(plantingRecordPage);
                
        // 收集所有的地块ID、农作物 ID 和种植计划 ID
        ArrayList<Long> landIds = new ArrayList<>();
        ArrayList<Long> cropIds = new ArrayList<>();
        ArrayList<Long> planIds = new ArrayList<>();
        for (PlantingRecord plantingRecord : page.getRecords()) {
            if (plantingRecord.getLandId() != null) {
                landIds.add(plantingRecord.getLandId());
            }
            if (plantingRecord.getCropId() != null) {
                cropIds.add(plantingRecord.getCropId());
            }
            if (plantingRecord.getPlanId() != null) {
                planIds.add(plantingRecord.getPlanId());
            }
        }
                
        // 批量查询地块信息
        ArrayList<Land> lands = new ArrayList<>();
        if (!landIds.isEmpty()) {
            lands = (ArrayList<Land>) landService.listByIds(landIds);
        }
                
        // 批量查询农作物信息
        ArrayList<Crop> crops = new ArrayList<>();
        if (!cropIds.isEmpty()) {
            crops = (ArrayList<Crop>) cropService.listByIds(cropIds);
        }
                
        // 批量查询种植计划信息
        ArrayList<PlantingPlan> plantingPlans = new ArrayList<>();
        if (!planIds.isEmpty()) {
            plantingPlans = (ArrayList<PlantingPlan>) plantingPlanService.listByIds(planIds);
        }
                
        // 转换为 Map 便于快速查找
        java.util.Map<Long, Land> landMap = lands.stream()
                .collect(java.util.stream.Collectors.toMap(Land::getLandId, land -> land));
        java.util.Map<Long, Crop> cropMap = crops.stream()
                .collect(java.util.stream.Collectors.toMap(Crop::getCropId, crop -> crop));
        java.util.Map<Long, PlantingPlan> planMap = plantingPlans.stream()
                .collect(java.util.stream.Collectors.toMap(PlantingPlan::getPlanId, plan -> plan));
                
        // 构建 VO 对象
        ArrayList<PlantingRecordVo> plantingRecordVos = new ArrayList<>();
        for (PlantingRecord plantingRecord : page.getRecords()) {
            PlantingRecordVo plantingRecordVo = new PlantingRecordVo();
            BeanUtils.copyProperties(plantingRecord, plantingRecordVo);
                    
            // 从 Map 中获取地块信息
            Land land = landMap.get(plantingRecord.getLandId());
            if (land != null) {
                plantingRecordVo.setLandName(land.getLandName());
                plantingRecordVo.setLocation(land.getLocation());
                plantingRecordVo.setArea(land.getArea());
            }
    
            // 从 Map 中获取农作物信息
            Crop crop = cropMap.get(plantingRecord.getCropId());
            if (crop != null) {
                plantingRecordVo.setCropName(crop.getCropName());
            }
                
            // 从 Map 中获取种植计划信息
            PlantingPlan plan = planMap.get(plantingRecord.getPlanId());
            if (plan != null) {
                plantingRecordVo.setPlanName(plan.getPlanName());
            }
                    
            plantingRecordVos.add(plantingRecordVo);
        }
                
        Page<PlantingRecordVo> plantingRecordVoPage = new Page<>(pageNum, pageSize, page.getTotal());
        plantingRecordVoPage.setRecords(plantingRecordVos);
        return Result.ok(plantingRecordVoPage);
    }

    @Override
    public Result delete(Long recordId) {
        return this.removeById(recordId)?Result.ok():Result.error("删除失败");
    }

    @Override
    public Result getAllAndCrops() {
        //获取所有地块id
        List<Long> landIds = this.list().stream().map(PlantingRecord::getLandId).toList();
        //获取所有农作物id
        List<Long> cropIds = this.list().stream().map(PlantingRecord::getCropId).toList();
        List<Land> lands = landService.listByIds(landIds);
        List<Crop> crops = cropService.listByIds(cropIds);
        HashMap<String, Object> map = new HashMap<>();
        map.put("landList", lands);
        map.put("cropList", crops);
        return Result.ok(map);
    }

    @Override
    public Result getGrowthPlantingRecordsByPage(Integer pageNum, Integer pageSize) {
        Page<PlantingRecord> plantingRecordPage = new Page<>(pageNum, pageSize);
//        Page<PlantingRecord> page = this.lambdaQuery().eq(PlantingRecord::getStatus, 0)
//                .page(plantingRecordPage);
        Page<PlantingRecord> page = this.lambdaQuery()
                .page(plantingRecordPage);

        // 收集所有的地块 ID、农作物 ID 和种植计划 ID
        ArrayList<Long> landIds = new ArrayList<>();
        ArrayList<Long> cropIds = new ArrayList<>();
        ArrayList<Long> planIds = new ArrayList<>();
        for (PlantingRecord plantingRecord : page.getRecords()) {
            if (plantingRecord.getLandId() != null) {
                landIds.add(plantingRecord.getLandId());
            }
            if (plantingRecord.getCropId() != null) {
                cropIds.add(plantingRecord.getCropId());
            }
            if (plantingRecord.getPlanId() != null) {
                planIds.add(plantingRecord.getPlanId());
            }
        }

        // 批量查询地块信息
        ArrayList<Land> lands = new ArrayList<>();
        if (!landIds.isEmpty()) {
            lands = (ArrayList<Land>) landService.listByIds(landIds);
        }

        // 批量查询农作物信息
        ArrayList<Crop> crops = new ArrayList<>();
        if (!cropIds.isEmpty()) {
            crops = (ArrayList<Crop>) cropService.listByIds(cropIds);
        }

        // 批量查询种植计划信息
        ArrayList<PlantingPlan> plantingPlans = new ArrayList<>();
        if (!planIds.isEmpty()) {
            plantingPlans = (ArrayList<PlantingPlan>) plantingPlanService.listByIds(planIds);
        }

        // 转换为 Map 便于快速查找
        java.util.Map<Long, Land> landMap = lands.stream()
                .collect(java.util.stream.Collectors.toMap(Land::getLandId, land -> land));
        java.util.Map<Long, Crop> cropMap = crops.stream()
                .collect(java.util.stream.Collectors.toMap(Crop::getCropId, crop -> crop));
        java.util.Map<Long, PlantingPlan> planMap = plantingPlans.stream()
                .collect(java.util.stream.Collectors.toMap(PlantingPlan::getPlanId, plan -> plan));

        // 构建 VO 对象
        ArrayList<PlantingRecordVo> plantingRecordVos = new ArrayList<>();
        for (PlantingRecord plantingRecord : page.getRecords()) {
            PlantingRecordVo plantingRecordVo = new PlantingRecordVo();
            BeanUtils.copyProperties(plantingRecord, plantingRecordVo);

            // 从 Map 中获取地块信息
            Land land = landMap.get(plantingRecord.getLandId());
            if (land != null) {
                plantingRecordVo.setLandName(land.getLandName());
                plantingRecordVo.setLocation(land.getLocation());
                plantingRecordVo.setArea(land.getArea());
            }

            // 从 Map 中获取农作物信息
            Crop crop = cropMap.get(plantingRecord.getCropId());
            if (crop != null) {
                plantingRecordVo.setCropName(crop.getCropName());
            }

            // 从 Map 中获取种植计划信息
            PlantingPlan plan = planMap.get(plantingRecord.getPlanId());
            if (plan != null) {
                plantingRecordVo.setPlanName(plan.getPlanName());
            }

            plantingRecordVos.add(plantingRecordVo);
        }

        Page<PlantingRecordVo> plantingRecordVoPage = new Page<>(pageNum, pageSize, page.getTotal());
        plantingRecordVoPage.setRecords(plantingRecordVos);
        return Result.ok(plantingRecordVoPage);
    }
}




