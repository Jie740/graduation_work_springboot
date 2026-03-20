package com.clj.controller;

import com.clj.domain.dto.PlantingPlanAdjustDto;
import com.clj.service.PlantingPlanAdjustService;
import com.clj.service.PlantingPlanService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plantingPlanAdjust")
public class PlantingPlanAdjustController {
    final PlantingPlanAdjustService plantingPlanAdjustService;
    @PostMapping("/addByAdmin")
    public Result add(@RequestBody PlantingPlanAdjustDto plantingPlanAdjustDto) {
        return plantingPlanAdjustService.add(plantingPlanAdjustDto);
    }
    @DeleteMapping("/delete/{plantingPlanAdjustId}")
    public Result delete(@PathVariable("plantingPlanAdjustId") Long plantingPlanAdjustId) {
        return plantingPlanAdjustService.delete(plantingPlanAdjustId);
    }
    @PutMapping("/update")
    public Result update(@RequestBody PlantingPlanAdjustDto plantingPlanAdjustDto) {
        return plantingPlanAdjustService.updatePlantingPlanAdjust(plantingPlanAdjustDto);
    }
    @PutMapping("/update/{plantingPlanAdjustId}/{status}")
    public Result updateStatus(@PathVariable("plantingPlanAdjustId") Long plantingPlanAdjustId,
                         @PathVariable("status") Integer status) {
        return plantingPlanAdjustService.updateStatus(plantingPlanAdjustId, status);
    }
    @GetMapping("/getPlantingPlanAdjustsByPage/{pageNum}/{pageSize}")
    public Result getPlantingPlanAdjustsByPage(@PathVariable("pageNum") Integer pageNum,@PathVariable Integer pageSize){
        return plantingPlanAdjustService.getPlantingPlanAdjustsByPage(pageNum, pageSize);
    }

    //根据计划名和申请人姓名查询计划调整列表

    @GetMapping("/searchPlantingPlanAdjustsByPage/{keyword}/{pageNum}/{pageSize}")
    public Result searchPlantingPlanAdjustsByPage(@PathVariable("keyword") String keyword,
                                                  @PathVariable("pageNum") Integer pageNum,
                                                  @PathVariable("pageSize") Integer pageSize){
        if (keyword == null){
            return plantingPlanAdjustService.getPlantingPlanAdjustsByPage(pageNum, pageSize);
        }
        return plantingPlanAdjustService.searchPlantingPlanAdjustsByPage(keyword, pageNum, pageSize);
    }

    @GetMapping("/getPlantingPlanAdjustsByAdjust/{adjustId}")
    public Result getPlantingPlanAdjustsByAdjustId(@PathVariable("adjustId") Long adjustId){
        return plantingPlanAdjustService.getPlantingPlanAdjustsByAdjustId(adjustId);
    }
}
