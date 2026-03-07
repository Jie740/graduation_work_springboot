package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.ContractorMaterialStock;
import com.clj.service.ContractorMaterialStockService;
import com.clj.mapper.ContractorMaterialStockMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【contractor_material_stock(承包人农资库存表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:44
*/
@Service
public class ContractorMaterialStockServiceImpl extends ServiceImpl<ContractorMaterialStockMapper, ContractorMaterialStock>
    implements ContractorMaterialStockService{

}




