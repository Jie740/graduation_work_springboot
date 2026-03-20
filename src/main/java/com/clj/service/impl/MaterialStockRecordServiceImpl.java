package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.MaterialStockRecord;
import com.clj.service.MaterialStockRecordService;
import com.clj.mapper.MaterialStockRecordMapper;
import com.clj.utils.Result;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【material_stock_record(农资出入库记录表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:12
*/
@Service
public class MaterialStockRecordServiceImpl extends ServiceImpl<MaterialStockRecordMapper, MaterialStockRecord>
    implements MaterialStockRecordService{

    @Override
    public Result add(MaterialStockRecord materialStockRecord) {
        return this.save(materialStockRecord)? Result.ok() : Result.error("添加失败");
    }
}




