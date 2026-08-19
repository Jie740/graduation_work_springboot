package com.clj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.common.result.Result;
import com.clj.domain.Land;
import com.clj.domain.PlantingPlan;
import com.clj.domain.dto.PlantingPlanDto;
import com.clj.domain.vo.PlantingPlanVo;
import com.clj.service.PlantingPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plantingPlan")
public class PlantingPlanController {
    private final PlantingPlanService plantingPlanService;
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> add(@RequestBody PlantingPlanDto plantingPlanDto) {
        plantingPlanService.add(plantingPlanDto);
        return Result.success();
    }
    @DeleteMapping("/delete/{plantingPlanId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> delete(@PathVariable("plantingPlanId") Long plantingPlanId) {
        plantingPlanService.delete(plantingPlanId);
        return Result.success();
    }
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> update(@RequestBody PlantingPlan plantingPlan) {
        plantingPlanService.updatePlantingPlan(plantingPlan);
        return Result.success();
    }
    @GetMapping("/getPlansByPage/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Page<PlantingPlanVo>> getPlantingPlansByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return Result.success(plantingPlanService.getPlantingPlansByPage(pageNum, pageSize));
    }
    @GetMapping("/searchPlansByPage/{keyword}/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Page<PlantingPlanVo>> searchPlantingPlansByPage(@PathVariable("keyword") String keyword
            , @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return Result.success(plantingPlanService.searchPlantingPlansByPage(keyword, pageNum, pageSize));
    }
    @PutMapping("/updateStatus/{planId}/{status}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> updateStatus(@PathVariable("planId") Long planId, @PathVariable("status") Integer status) {
        plantingPlanService.updateStatus(planId, status);
        return Result.success();
    }

    @GetMapping("/getPlantingPlanById/{planId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<PlantingPlanVo> getPlantingPlanById(@PathVariable("planId") Long planId) {
        return Result.success(plantingPlanService.getPlantingPlanById(planId));
    }

    //获取已发布的计划
    @GetMapping("/getPublishedPlantingPlans")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<List<PlantingPlanVo>> getPublishedPlantingPlans() {
        return Result.success(plantingPlanService.getPublishedPlantingPlans());
    }

    //获得对应用户的已发布的计划
    @GetMapping("/getPublishedPlantingPlanByUserId")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<List<PlantingPlanVo>> getPlantingPlanByUserId() {
        return Result.success(plantingPlanService.getPublishedPlantingPlanByUserId());
    }
    @GetMapping("/getAll")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<List<PlantingPlan>> getAll() {
        return Result.success(plantingPlanService.list());
    }

    @GetMapping("/getByLandId/{landId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<PlantingPlan> getByLandId(@PathVariable("landId") Long landId) {
        return Result.success(plantingPlanService.getByLandId(landId));
    }

    @GetMapping("/getMyPlans")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<List<PlantingPlanVo>> getMyPlans() {
        return Result.success(plantingPlanService.getMyPlans());
    }

    //根据计划ID查询用户名
    @GetMapping("/getUserNameByPlanId/{planId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Map<String, String>> getUserNameByPlanId(@PathVariable("planId") Long planId) {
        return Result.success(plantingPlanService.getUserNameByPlanId(planId));
    }
}
