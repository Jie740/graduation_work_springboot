package com.clj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.common.result.Result;
import com.clj.domain.CropGrowthRecord;
import com.clj.service.CropGrowthRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cropGrowthRecord")
public class CropGrowthRecordController {
    final CropGrowthRecordService cropGrowthRecordService;
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> add(@RequestBody CropGrowthRecord cropGrowthRecord) {
        cropGrowthRecordService.add(cropGrowthRecord);
        return Result.success();
    }
    @DeleteMapping("/delete/{cropGrowthRecordId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> delete(@PathVariable("cropGrowthRecordId") Integer cropGrowthRecordId) {
        cropGrowthRecordService.delete(cropGrowthRecordId);
        return Result.success();
    }
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> updateCropGrowthRecord(@RequestBody CropGrowthRecord cropGrowthRecord) {
        cropGrowthRecordService.updateCropGrowthRecord(cropGrowthRecord);
        return Result.success();
    }
    @GetMapping("/getCropGrowthRecordsByPage/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<CropGrowthRecord>> getCropGrowthRecordsByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return Result.success(cropGrowthRecordService.getCropGrowthRecordsByPage(pageNum, pageSize));
    }
    @GetMapping("/searchCropGrowthRecordsByPage/{keyword}/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<CropGrowthRecord>> searchCropGrowthRecordsByPage(@PathVariable("keyword") String keyword
            ,@PathVariable("pageNum") Integer pageNum,@PathVariable("pageSize") Integer pageSize){
        return Result.success(cropGrowthRecordService.searchCropGrowthRecordsByPage(keyword, pageNum, pageSize));
    }
}
