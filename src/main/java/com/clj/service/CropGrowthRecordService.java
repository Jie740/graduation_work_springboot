package com.clj.service;

import com.clj.domain.CropGrowthRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
* @author ajie
* @description 针对表【crop_growth_record(农作物生长记录表)】的数据库操作Service
* @createDate 2026-03-02 20:08:39
*/
public interface CropGrowthRecordService extends IService<CropGrowthRecord> {
    void add(CropGrowthRecord cropGrowthRecord);
    void delete(Integer cropGrowthRecordId);
    void updateCropGrowthRecord(CropGrowthRecord cropGrowthRecord);
    Page<CropGrowthRecord> getCropGrowthRecordsByPage(Integer pageNum, Integer pageSize);
    Page<CropGrowthRecord> searchCropGrowthRecordsByPage(String keyword, Integer pageNum, Integer pageSize);
}
