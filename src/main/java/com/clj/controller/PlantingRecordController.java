package com.clj.controller;

import com.clj.domain.PlantingRecord;
import com.clj.domain.dto.PlantingRecordDto;
import com.clj.service.PlantingRecordService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plantingRecord")
public class PlantingRecordController {
    final PlantingRecordService plantingRecordService;
    @PostMapping("/add")
    public Result add(@RequestBody PlantingRecordDto plantingRecordDto){
        return plantingRecordService.add(plantingRecordDto);
    }
    @PutMapping("/update")
    public Result update(@RequestBody PlantingRecordDto plantingRecordDto){
        return plantingRecordService.updatePlantingRecord(plantingRecordDto);
    }
    @GetMapping("/getPlantingRecordsByPage/{pageNum}/{pageSize}")
    public Result getPlantingRecordsByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize){
        return plantingRecordService.getPlantingRecordsByPage(pageNum, pageSize);
    }
    @DeleteMapping("/delete/{recordId}")
    public Result delete(@PathVariable("recordId") Long recordId){
        return plantingRecordService.delete(recordId);
    }

    @GetMapping("/getAllAndCrops")
    public Result getAllAndCrops(){
        return plantingRecordService.getAllAndCrops();
    }

    @GetMapping("/getGrowthPlantingRecordsByPage/{pageNum}/{pageSize}")
    public Result getGrowthPlantingRecordsByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize){
        return plantingRecordService.getGrowthPlantingRecordsByPage(pageNum, pageSize);
    }

    @GetMapping("/getMyPlantingRecords/{pageNum}/{pageSize}")
    public Result getMyPlantingRecords(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize){
        return plantingRecordService.getMyPlantingRecords(pageNum, pageSize);
    }

}
