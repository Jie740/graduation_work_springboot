package com.clj.service;

import com.clj.domain.PlantingRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.dto.PlantingRecordDto;
import com.clj.utils.Result;

/**
* @author ajie
* @description 针对表【planting_record(地块种植记录表)】的数据库操作Service
* @createDate 2026-03-02 20:07:58
*/
public interface PlantingRecordService extends IService<PlantingRecord> {


    Result add(PlantingRecordDto plantingRecordDto);

    Result updatePlantingRecord(PlantingRecordDto plantingRecordDto);


    Result getPlantingRecordsByPage(Integer pageNum, Integer pageSize);

    Result delete(Long recordId);

    Result getAllAndCrops();

    Result getGrowthPlantingRecordsByPage(Integer pageNum, Integer pageSize);
}
