package com.clj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.common.result.Result;
import com.clj.domain.ContractorMaterialStock;
import com.clj.domain.dto.FarmOperationDto;
import com.clj.domain.vo.FarmOperationRecordVo;
import com.clj.service.ContractorMaterialStockService;
import com.clj.service.FarmOperationRecordService;
import com.clj.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/farmOperation")
public class FarmOperationRecordController {
    final FarmOperationRecordService farmOperationRecordService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> add(@RequestBody FarmOperationDto farmOperationDto){
        farmOperationRecordService.add(farmOperationDto);
        return Result.success();
    }
    @GetMapping("/getOperationPageByRecordId/{recordId}/{page}/{size}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<FarmOperationRecordVo>> getFarmOperationRecordById(
            @PathVariable("recordId") Long recordId,
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size) {
        return Result.success(farmOperationRecordService.getFarmOperationRecordById(recordId, page, size));
    }

    @DeleteMapping("/delete/{operationId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> delete(@PathVariable("operationId") Long operationId){
        farmOperationRecordService.delete(operationId);
        return Result.success();
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> update(@RequestBody FarmOperationDto farmOperationDto){
        farmOperationRecordService.updateFamrOperation(farmOperationDto);
        return Result.success();
    }
}
