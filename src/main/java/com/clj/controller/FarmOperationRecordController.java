package com.clj.controller;

import com.clj.domain.ContractorMaterialStock;
import com.clj.domain.dto.FarmOperationDto;
import com.clj.service.ContractorMaterialStockService;
import com.clj.service.FarmOperationRecordService;
import com.clj.service.UserService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/farmOperation")
public class FarmOperationRecordController {
    final FarmOperationRecordService farmOperationRecordService;

    @PostMapping("/add")
    public Result add(@RequestBody FarmOperationDto farmOperationDto){
        return farmOperationRecordService.add(farmOperationDto);
    }
    @GetMapping("/getOperationPageByRecordId/{recordId}/{page}/{size}")
    public Result getFarmOperationRecordById(
            @PathVariable("recordId") Long recordId,
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size) {
        return farmOperationRecordService.getFarmOperationRecordById(recordId, page, size);
    }

    @DeleteMapping("/delete/{operationId}")
    public Result delete(@PathVariable("operationId") Long operationId){
        return farmOperationRecordService.delete(operationId);
    }

    @PutMapping("/update")
    public Result update(@RequestBody FarmOperationDto farmOperationDto){
        return farmOperationRecordService.updateFamrOperation(farmOperationDto);
    }
}
