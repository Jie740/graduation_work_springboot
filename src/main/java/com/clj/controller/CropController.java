package com.clj.controller;

import com.clj.domain.Crop;
import com.clj.service.CropService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crop")
public class CropController {
    final CropService cropService;
    @PostMapping("/addCrop")
    public Result add(@RequestBody Crop crop) {
        return cropService.add(crop);
    }
    @DeleteMapping("/deleteCrop/{cropId}")
    public Result delete(@PathVariable("cropId") Integer cropId) {
        return cropService.delete(cropId);
    }
    @PutMapping("/updateCrop")
    public Result update(@RequestBody Crop crop) {
        return cropService.updateCrop(crop);
    }
    @GetMapping("/getCropsByPage/{pageNum}/{pageSize}")
    public Result getCropsByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return cropService.getCropsByPage(pageNum, pageSize);
    }

    //筛选条件：农作物名、类型

    @GetMapping("/searchCropsByPage/{keyword}/{pageNum}/{pageSIze}")
    public Result searchCropsByPage(@PathVariable("keyword") String keyword, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSIze") Integer pageSize) {
        return cropService.searchCropsByPage(keyword, pageNum, pageSize);
    }

    @GetMapping("/getAll")
    public Result getAll() {
        return Result.ok(cropService.list());
    }
}
