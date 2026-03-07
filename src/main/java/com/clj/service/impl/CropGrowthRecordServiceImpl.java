package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.CropGrowthRecord;
import com.clj.service.CropGrowthRecordService;
import com.clj.mapper.CropGrowthRecordMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【crop_growth_record(农作物生长记录表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:39
*/
@Service
public class CropGrowthRecordServiceImpl extends ServiceImpl<CropGrowthRecordMapper, CropGrowthRecord>
    implements CropGrowthRecordService{

}




