package com.clj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.common.result.Result;
import com.clj.domain.PlantingRecord;
import com.clj.domain.dto.PlantingRecordDto;
import com.clj.domain.vo.PlantingRecordVo;
import com.clj.service.PlantingRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plantingRecord")
public class PlantingRecordController {
    final PlantingRecordService plantingRecordService;
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> add(@RequestBody PlantingRecordDto plantingRecordDto){
        plantingRecordService.add(plantingRecordDto);
        return Result.success();
    }
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> update(@RequestBody PlantingRecordDto plantingRecordDto){
        plantingRecordService.updatePlantingRecord(plantingRecordDto);
        return Result.success();
    }
    @GetMapping("/getPlantingRecordsByPage/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Page<PlantingRecordVo>> getPlantingRecordsByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize){
        return Result.success(plantingRecordService.getPlantingRecordsByPage(pageNum, pageSize));
    }
    @DeleteMapping("/delete/{recordId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> delete(@PathVariable("recordId") Long recordId){
        plantingRecordService.delete(recordId);
        return Result.success();
    }

    @GetMapping("/getAllAndCrops")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Map<String, Object>> getAllAndCrops(){
        return Result.success(plantingRecordService.getAllAndCrops());
    }

    @GetMapping("/getGrowthPlantingRecordsByPage/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Page<PlantingRecordVo>> getGrowthPlantingRecordsByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize){
        return Result.success(plantingRecordService.getGrowthPlantingRecordsByPage(pageNum, pageSize));
    }

    @GetMapping("/getMyPlantingRecords/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Page<PlantingRecordVo>> getMyPlantingRecords(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize){
        return Result.success(plantingRecordService.getMyPlantingRecords(pageNum, pageSize));
    }

}
