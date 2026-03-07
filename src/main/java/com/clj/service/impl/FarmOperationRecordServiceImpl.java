package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.FarmOperationRecord;
import com.clj.service.FarmOperationRecordService;
import com.clj.mapper.FarmOperationRecordMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【farm_operation_record(农事活动记录表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:24
*/
@Service
public class FarmOperationRecordServiceImpl extends ServiceImpl<FarmOperationRecordMapper, FarmOperationRecord>
    implements FarmOperationRecordService{

}




