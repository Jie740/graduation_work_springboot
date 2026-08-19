package com.clj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.common.result.Result;
import com.clj.domain.ContractorMaterialStock;
import com.clj.domain.LandAllocation;
import com.clj.domain.dto.ContractorMaterialStockDto;
import com.clj.domain.dto.LandAllocationDto;
import com.clj.domain.vo.ContractorMaterialStockVo;
import com.clj.service.ContractorMaterialStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contractorMaterialStock")
public class ContractorMaterialStockController {
    final ContractorMaterialStockService contractorMaterialStockService;
//    @PostMapping("/add")
//    public Result add(@RequestBody LandAllocation landAllocation) {
//        return landAllocationService.addLandAllocation(landAllocation);
//    }

    @DeleteMapping("/delete/{contractorMaterialStockId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> delete(@PathVariable("contractorMaterialStockId") Long contractorMaterialId) {
        contractorMaterialStockService.delete(contractorMaterialId) ;
        return Result.success();
    }
  @PutMapping("/updateStock")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> update(@RequestBody ContractorMaterialStockDto contractorMaterialStockDto) {
        contractorMaterialStockService.updateContractorMaterialStock(contractorMaterialStockDto);
        return Result.success();
    }


    @GetMapping("/getLandAllocationByPage/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Page<ContractorMaterialStockVo>> getLandAllocationByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return Result.success(contractorMaterialStockService.getByPage(pageNum, pageSize));
    }

    //根据用户ID获取农资库存 信息
    //keyword：农资名
    @GetMapping("/getByUserId/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Page<ContractorMaterialStockVo>> getByUserId(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return Result.success(contractorMaterialStockService.getByUserId(keyword,pageNum,pageSize));
    }
    @GetMapping("/searchLandAllocationInfoByPage/{keyword}/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Page<ContractorMaterialStockVo>> searchLandAllocationInfoByPage(@PathVariable("keyword") String keyword,
                                                 @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return Result.success(contractorMaterialStockService.searchByPage(keyword, pageNum, pageSize));
    }
}
