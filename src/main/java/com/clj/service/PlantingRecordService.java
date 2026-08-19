package com.clj.service;

import com.clj.domain.PlantingRecord;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.dto.PlantingRecordDto;
import com.clj.domain.vo.PlantingRecordVo;

import java.util.Map;

/**
* @author ajie
* @description 针对表【planting_record(地块种植记录表)】的数据库操作Service
* @createDate 2026-03-02 20:07:58
*/
public interface PlantingRecordService extends IService<PlantingRecord> {


    void add(PlantingRecordDto plantingRecordDto);

    void updatePlantingRecord(PlantingRecordDto plantingRecordDto);


    Page<PlantingRecordVo> getPlantingRecordsByPage(Integer pageNum, Integer pageSize);

    void delete(Long recordId);

    Map<String, Object> getAllAndCrops();

    Page<PlantingRecordVo> getGrowthPlantingRecordsByPage(Integer pageNum, Integer pageSize);

    Page<PlantingRecordVo> getMyPlantingRecords(Integer pageNum, Integer pageSize);
}
