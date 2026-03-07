package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.ProcessingRecord;
import com.clj.service.ProcessingRecordService;
import com.clj.mapper.ProcessingRecordMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【processing_record(加工记录表)】的数据库操作Service实现
* @createDate 2026-03-02 20:07:55
*/
@Service
public class ProcessingRecordServiceImpl extends ServiceImpl<ProcessingRecordMapper, ProcessingRecord>
    implements ProcessingRecordService{

}




