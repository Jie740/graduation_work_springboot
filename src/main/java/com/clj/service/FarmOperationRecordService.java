package com.clj.service;

import com.clj.domain.FarmOperationRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.dto.FarmOperationDto;
import com.clj.utils.Result;

/**
* @author ajie
* @description 针对表【farm_operation_record(农事活动记录表)】的数据库操作Service
* @createDate 2026-03-02 20:08:24
*/
public interface FarmOperationRecordService extends IService<FarmOperationRecord> {

    Result add(FarmOperationDto farmOperationDto);

    Result getFarmOperationRecordById(Long recordId, Integer page, Integer size);

    Result updateFamrOperation(FarmOperationDto farmOperationDto);

    Result delete(Long operationId);
}
