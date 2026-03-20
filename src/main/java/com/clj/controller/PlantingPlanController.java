package com.clj.controller;

import com.clj.domain.Land;
import com.clj.domain.PlantingPlan;
import com.clj.domain.dto.PlantingPlanDto;
import com.clj.service.PlantingPlanService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plantingPlan")
public class PlantingPlanController {
    private final PlantingPlanService plantingPlanService;
    @PostMapping("/add")
    public Result add(@RequestBody PlantingPlanDto plantingPlanDto) {
        return plantingPlanService.add(plantingPlanDto);
    }
    @DeleteMapping("/delete/{plantingPlanId}")
    public Result delete(@PathVariable("plantingPlanId") Long plantingPlanId) {
        return plantingPlanService.delete(plantingPlanId);
    }
    @PutMapping("/update")
    public Result update(@RequestBody PlantingPlan plantingPlan) {
        return plantingPlanService.updatePlantingPlan(plantingPlan);
    }
    @GetMapping("/getPlansByPage/{pageNum}/{pageSize}")
    public Result getPlantingPlansByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return plantingPlanService.getPlantingPlansByPage(pageNum, pageSize);
    }
    @GetMapping("/searchPlansByPage/{keyword}/{pageNum}/{pageSize}")
    public Result searchPlantingPlansByPage(@PathVariable("keyword") String keyword
            , @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return plantingPlanService.searchPlantingPlansByPage(keyword, pageNum, pageSize);
    }
    @PutMapping("/updateStatus/{planId}/{status}")
    public Result updateStatus(@PathVariable("planId") Long planId, @PathVariable("status") Integer status) {
        return plantingPlanService.updateStatus(planId, status);
    }

    @GetMapping("/getPlantingPlanById/{planId}")
    public Result getPlantingPlanById(@PathVariable("planId") Long planId) {
        return plantingPlanService.getPlantingPlanById(planId);
    }

    //获取已发布的计划
    @GetMapping("/getPublishedPlantingPlans")
    public Result getPublishedPlantingPlans() {
        return plantingPlanService.getPublishedPlantingPlans();
    }

    @GetMapping("/getAll")
    public Result getAll() {
        return Result.ok(plantingPlanService.list());
    }

    @GetMapping("/getByLandId/{landId}")
    public Result getByLandId(@PathVariable("landId") Long landId) {
        return plantingPlanService.getByLandId(landId);
    }
}
