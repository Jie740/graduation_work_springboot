package com.clj.service;

import com.clj.domain.CropGrowthRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.utils.Result;

/**
* @author ajie
* @description 针对表【crop_growth_record(农作物生长记录表)】的数据库操作Service
* @createDate 2026-03-02 20:08:39
*/
public interface CropGrowthRecordService extends IService<CropGrowthRecord> {
    Result add(CropGrowthRecord cropGrowthRecord);
    Result delete(Integer cropGrowthRecordId);
    Result updateCropGrowthRecord(CropGrowthRecord cropGrowthRecord);
    Result getCropGrowthRecordsByPage(Integer pageNum, Integer pageSize);
    Result searchCropGrowthRecordsByPage(String keyword, Integer pageNum, Integer pageSize);
}
