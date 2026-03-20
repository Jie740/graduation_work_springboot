package com.clj.controller;

import com.clj.domain.CropGrowthRecord;
import com.clj.service.CropGrowthRecordService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cropGrowthRecord")
public class CropGrowthRecordController {
    final CropGrowthRecordService cropGrowthRecordService;
    @PostMapping("/add")
    public Result add(@RequestBody CropGrowthRecord cropGrowthRecord) {
        return cropGrowthRecordService.add(cropGrowthRecord);
    }
    @DeleteMapping("/delete/{cropGrowthRecordId}")
    public Result delete(@PathVariable("cropGrowthRecordId") Integer cropGrowthRecordId) {
        return cropGrowthRecordService.delete(cropGrowthRecordId);
    }
    @PutMapping("/update")
    public Result updateCropGrowthRecord(@RequestBody CropGrowthRecord cropGrowthRecord) {
        return cropGrowthRecordService.updateCropGrowthRecord(cropGrowthRecord);
    }
    @GetMapping("/getCropGrowthRecordsByPage/{pageNum}/{pageSize}")
    public Result getCropGrowthRecordsByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return cropGrowthRecordService.getCropGrowthRecordsByPage(pageNum, pageSize);
    }
    @GetMapping("/searchCropGrowthRecordsByPage/{keyword}/{pageNum}/{pageSize}")
    public Result searchCropGrowthRecordsByPage(@PathVariable("keyword") String keyword
            ,@PathVariable("pageNum") Integer pageNum,@PathVariable("pageSize") Integer pageSize){
        return cropGrowthRecordService.searchCropGrowthRecordsByPage(keyword, pageNum, pageSize);
    }
}
