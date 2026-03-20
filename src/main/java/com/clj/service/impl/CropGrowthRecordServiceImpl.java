package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.CropGrowthRecord;
import com.clj.service.CropGrowthRecordService;
import com.clj.mapper.CropGrowthRecordMapper;
import com.clj.service.LandService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.glassfish.jaxb.core.v2.TODO;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【crop_growth_record(农作物生长记录表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:39
*/
@Service
@RequiredArgsConstructor
public class CropGrowthRecordServiceImpl extends ServiceImpl<CropGrowthRecordMapper, CropGrowthRecord>
    implements CropGrowthRecordService{

    final LandService landService;

    @Override
    public Result add(CropGrowthRecord cropGrowthRecord) {
        return this.save(cropGrowthRecord)? Result.ok() : Result.error("添加失败");
    }

    @Override
    public Result delete(Integer cropGrowthRecordId) {
        return this.removeById(cropGrowthRecordId)? Result.ok() : Result.error("删除失败");
    }

    @Override
    public Result updateCropGrowthRecord(CropGrowthRecord cropGrowthRecord) {
        return this.updateById(cropGrowthRecord)? Result.ok() : Result.error("更新失败");
    }

    @Override
    public Result getCropGrowthRecordsByPage(Integer pageNum, Integer pageSize) {
        Page<CropGrowthRecord> page = new Page<>(pageNum, pageSize);
        return Result.ok(this.lambdaQuery().page(page));
    }

//    TODO: 复杂查询
//    查询条件：
    @Override
    public Result searchCropGrowthRecordsByPage(String keyword, Integer pageNum, Integer pageSize) {
        if (keyword==null){
            return getCropGrowthRecordsByPage(pageNum, pageSize);
        }
        Page<CropGrowthRecord> page = new Page<>(pageNum, pageSize);
        return Result.ok(this.lambdaQuery().

                page( page));
    }
}




