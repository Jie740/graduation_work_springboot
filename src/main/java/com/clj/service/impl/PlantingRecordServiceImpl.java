package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.PlantingRecord;
import com.clj.service.PlantingRecordService;
import com.clj.mapper.PlantingRecordMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【planting_record(地块种植记录表)】的数据库操作Service实现
* @createDate 2026-03-02 20:07:58
*/
@Service
public class PlantingRecordServiceImpl extends ServiceImpl<PlantingRecordMapper, PlantingRecord>
    implements PlantingRecordService{

}




