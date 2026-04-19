package com.clj.service;

import com.clj.domain.MaterialStockRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.utils.Result;

/**
* @author ajie
* @description 针对表【material_stock_record(农资出入库记录表)】的数据库操作Service
* @createDate 2026-03-02 20:08:12
*/
public interface MaterialStockRecordService extends IService<MaterialStockRecord> {

    Result add(MaterialStockRecord materialStockRecord);

    Result delete(Long stockRecordId);

    Result update(MaterialStockRecord materialStockRecord);

    Result getByPage(String keyword, Integer pageNum, Integer pageSize);
}
