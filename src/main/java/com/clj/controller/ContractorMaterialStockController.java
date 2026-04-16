package com.clj.controller;

import com.clj.domain.ContractorMaterialStock;
import com.clj.domain.LandAllocation;
import com.clj.domain.dto.ContractorMaterialStockDto;
import com.clj.domain.dto.LandAllocationDto;
import com.clj.service.ContractorMaterialStockService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
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
    public Result delete(@PathVariable("contractorMaterialStockId") Long contractorMaterialId) {
        return contractorMaterialStockService.delete(contractorMaterialId) ;
    }
  @PutMapping("/updateStock")
    public Result update(@RequestBody ContractorMaterialStockDto contractorMaterialStockDto) {
        return contractorMaterialStockService.updateContractorMaterialStock(contractorMaterialStockDto);
    }


    @GetMapping("/getLandAllocationByPage/{pageNum}/{pageSize}")
    public Result getLandAllocationByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return contractorMaterialStockService.getByPage(pageNum, pageSize);
    }

    //根据用户ID获取农资库存 信息
    //keyword：农资名
    @GetMapping("/getByUserId/{pageNum}/{pageSize}")
    public Result getByUserId(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return contractorMaterialStockService.getByUserId(keyword,pageNum,pageSize);
    }
    @GetMapping("/searchLandAllocationInfoByPage/{keyword}/{pageNum}/{pageSize}")
    public Result searchLandAllocationInfoByPage(@PathVariable("keyword") String keyword,
                                                 @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return contractorMaterialStockService.searchByPage(keyword, pageNum, pageSize);
    }
}
