package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.ContractorMaterialStock;
import com.clj.service.ContractorMaterialStockService;
import com.clj.mapper.ContractorMaterialStockMapper;
import com.clj.utils.Result;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【contractor_material_stock(承包人农资库存表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:44
*/
@Service
public class ContractorMaterialStockServiceImpl extends ServiceImpl<ContractorMaterialStockMapper, ContractorMaterialStock>
    implements ContractorMaterialStockService{

    @Override
    public Result add(ContractorMaterialStock contractorMaterialStock) {
        ContractorMaterialStock one = this.lambdaQuery().eq(ContractorMaterialStock::getMaterialId, contractorMaterialStock.getMaterialId())
                .eq(ContractorMaterialStock::getUserId, contractorMaterialStock.getUserId())
                .one();
        //增加库存
        if (one != null){
           return this.lambdaUpdate().eq(ContractorMaterialStock::getMaterialId, contractorMaterialStock.getMaterialId())
                    .eq(ContractorMaterialStock::getUserId, contractorMaterialStock.getUserId())
                    .set(ContractorMaterialStock::getStock, one.getStock()+contractorMaterialStock.getStock())
                    .update()? Result.ok() : Result.error("保存失败");
        }
        return this.save(contractorMaterialStock)? Result.ok() : Result.error("保存失败");
    }
}




