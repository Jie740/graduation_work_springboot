package com.clj.service;

import com.clj.domain.MaterialStockRecord;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.vo.MaterialStockRecordVo;

/**
* @author ajie
* @description 针对表【material_stock_record(农资出入库记录表)】的数据库操作Service
* @createDate 2026-03-02 20:08:12
*/
public interface MaterialStockRecordService extends IService<MaterialStockRecord> {

    void add(MaterialStockRecord materialStockRecord);

    void delete(Long stockRecordId);

    void update(MaterialStockRecord materialStockRecord);

    Page<MaterialStockRecordVo> getByPage(String keyword, Integer pageNum, Integer pageSize);
}
