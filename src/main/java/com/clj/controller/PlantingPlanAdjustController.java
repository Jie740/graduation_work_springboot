package com.clj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.common.result.Result;
import com.clj.domain.dto.PlantingPlanAdjustDto;
import com.clj.domain.vo.PlantingPlanAdjustDetailVo;
import com.clj.domain.vo.PlantingPlanAdjustVo;
import com.clj.service.PlantingPlanAdjustService;
import com.clj.service.PlantingPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plantingPlanAdjust")
public class PlantingPlanAdjustController {
    final PlantingPlanAdjustService plantingPlanAdjustService;
    @PostMapping("/addByAdmin")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> add(@RequestBody PlantingPlanAdjustDto plantingPlanAdjustDto) {
        plantingPlanAdjustService.add(plantingPlanAdjustDto);
        return Result.success();
    }
    @DeleteMapping("/delete/{plantingPlanAdjustId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> delete(@PathVariable("plantingPlanAdjustId") Long plantingPlanAdjustId) {
        plantingPlanAdjustService.delete(plantingPlanAdjustId);
        return Result.success();
    }
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> update(@RequestBody PlantingPlanAdjustDto plantingPlanAdjustDto) {
        plantingPlanAdjustService.updatePlantingPlanAdjust(plantingPlanAdjustDto);
        return Result.success();
    }
    @PutMapping("/update/{plantingPlanAdjustId}/{status}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> updateStatus(@PathVariable("plantingPlanAdjustId") Long plantingPlanAdjustId,
                         @PathVariable("status") Integer status) {
        plantingPlanAdjustService.updateStatus(plantingPlanAdjustId, status);
        return Result.success();
    }
    @GetMapping("/getPlantingPlanAdjustsByPage/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Page<PlantingPlanAdjustVo>> getPlantingPlanAdjustsByPage(@PathVariable("pageNum") Integer pageNum,@PathVariable Integer pageSize){
        return Result.success(plantingPlanAdjustService.getPlantingPlanAdjustsByPage(pageNum, pageSize));
    }

    @GetMapping("/cancel")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> cancel(@RequestParam("adjustId") Long adjustId){
        plantingPlanAdjustService.cancel(adjustId);
        return Result.success();
    }
    //根据计划名和申请人姓名查询计划调整列表
    @GetMapping("/searchPlantingPlanAdjustsByPage/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Page<PlantingPlanAdjustVo>> searchPlantingPlanAdjustsByPage(
            @PathVariable("pageNum") Integer pageNum,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "keyword", required = false) String keyword){
        if (keyword == null || keyword.trim().isEmpty()){
            return Result.success(plantingPlanAdjustService.getPlantingPlanAdjustsByPage(pageNum, pageSize));
        }
        return Result.success(plantingPlanAdjustService.searchPlantingPlanAdjustsByPage(keyword, pageNum, pageSize));
    }

    @GetMapping("/getPlantingPlanAdjustsByAdjust/{adjustId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<PlantingPlanAdjustDetailVo> getPlantingPlanAdjustsByAdjustId(@PathVariable("adjustId") Long adjustId){
        return Result.success(plantingPlanAdjustService.getPlantingPlanAdjustsByAdjustId(adjustId));
    }

    //根据用户ID查询申请列表
    @GetMapping("/getPlantingPlanAdjustsByUserIdPage/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Page<PlantingPlanAdjustVo>> getPlantingPlanAdjustsByUserId(
            @PathVariable("pageNum") Integer pageNum,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "keyword", required = false) String keyword
    ){
        if (keyword == null || keyword.trim().isEmpty()){
            return Result.success(plantingPlanAdjustService.getPlantingPlanAdjustsByUserIdPage(pageNum, pageSize));
        }
        return Result.success(plantingPlanAdjustService.getPlantingPlanAdjustsByUser(keyword,pageNum, pageSize));
    }
}
