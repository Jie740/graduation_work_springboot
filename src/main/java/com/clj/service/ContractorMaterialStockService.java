package com.clj.service;

import com.clj.domain.ContractorMaterialStock;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.utils.Result;

/**
* @author ajie
* @description 针对表【contractor_material_stock(承包人农资库存表)】的数据库操作Service
* @createDate 2026-03-02 20:08:44
*/
public interface ContractorMaterialStockService extends IService<ContractorMaterialStock> {

    Result add(ContractorMaterialStock contractorMaterialStock);
}
