package com.clj.service;

import com.clj.domain.ContractorMaterialStock;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.dto.ContractorMaterialStockDto;
import com.clj.domain.vo.ContractorMaterialStockVo;

/**
* @author ajie
* @description 针对表【contractor_material_stock(承包人农资库存表)】的数据库操作Service
* @createDate 2026-03-02 20:08:44
*/
public interface ContractorMaterialStockService extends IService<ContractorMaterialStock> {

    void add(ContractorMaterialStock contractorMaterialStock);

    void delete(Long contractorMaterialId);

    void updateContractorMaterialStock(ContractorMaterialStockDto contractorMaterialStockDto);

    Page<ContractorMaterialStockVo> getByPage(Integer pageNum, Integer pageSize);

    Page<ContractorMaterialStockVo> searchByPage(String keyword, Integer pageNum, Integer pageSize);

    Page<ContractorMaterialStockVo> getByUserId(String keyword,Integer pageNum,Integer pageSize);
}
