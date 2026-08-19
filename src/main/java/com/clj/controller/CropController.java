package com.clj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.common.result.Result;
import com.clj.domain.Crop;
import com.clj.service.CropService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crop")
public class CropController {
    final CropService cropService;
    @PostMapping("/addCrop")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> add(@RequestBody Crop crop) {
        cropService.add(crop);
        return Result.success();
    }
    @DeleteMapping("/deleteCrop/{cropId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> delete(@PathVariable("cropId") Integer cropId) {
        cropService.delete(cropId);
        return Result.success();
    }
    @PutMapping("/updateCrop")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> update(@RequestBody Crop crop) {
        cropService.updateCrop(crop);
        return Result.success();
    }
    @GetMapping("/getCropsByPage/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<Crop>> getCropsByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return Result.success(cropService.getCropsByPage(pageNum, pageSize));
    }

    //筛选条件：农作物名、类型

    @GetMapping("/searchCropsByPage/{keyword}/{pageNum}/{pageSIze}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<Crop>> searchCropsByPage(@PathVariable("keyword") String keyword, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSIze") Integer pageSize) {
        return Result.success(cropService.searchCropsByPage(keyword, pageNum, pageSize));
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<List<Crop>> getAll() {
        return Result.success(cropService.list());
    }
}
