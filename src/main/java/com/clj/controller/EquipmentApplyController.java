package com.clj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.common.result.Result;
import com.clj.domain.dto.EquipmentApplyDto;
import com.clj.domain.vo.EquipmentApplyVo;
import com.clj.domain.vo.EquipmentDetailVo;
import com.clj.service.EquipmentApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/equipmentApply")
public class EquipmentApplyController {
    final EquipmentApplyService equipmentApplyService;
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Void> add(@RequestBody EquipmentApplyDto equipmentApplyDto){
        equipmentApplyService.add(equipmentApplyDto);
        return Result.success();
    }
    @DeleteMapping("/delete/{applyId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Void> delete(@PathVariable("applyId") Integer applyId){
        equipmentApplyService.delete(applyId);
        return Result.success();
    }
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> update(@RequestBody EquipmentApplyDto equipmentApplyDto){
        equipmentApplyService.updateApply(equipmentApplyDto);
        return Result.success();
    }

    @GetMapping("/getApplyByPage/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<EquipmentApplyVo>> getApplyByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize){
        return Result.success(equipmentApplyService.getApplyByPage(pageNum, pageSize));
    }

    @GetMapping("/searchApplyByPage/{keyword}/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<EquipmentApplyVo>> getApplyByPage(@PathVariable("keyword") String keyword, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize){
        return Result.success(equipmentApplyService.searchApplyByPage(keyword, pageNum, pageSize));
    }

    @PutMapping("/update/{applyId}/{status}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> update(@PathVariable("applyId") Long applyId,
                         @PathVariable("status") Integer status){
        equipmentApplyService.updateApplyStatus(applyId, status);
        return Result.success();
    }
    @GetMapping("/getMaterialApplyById/{applyId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<EquipmentApplyVo> getById(@PathVariable("applyId") Long applyId){
        return Result.success(equipmentApplyService.getApplyVoById(applyId));
    }

    @GetMapping("/getEquipmentNameAndTypeNameById/{applyId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<EquipmentDetailVo> getEquipmentNameAndTypeNameById(@PathVariable("applyId") Long applyId){
        return Result.success(equipmentApplyService.getEquipmentNameAndTypeNameById(applyId));
    }

    // 根据用户ID分页查询我的设备申请
    @GetMapping("/getMyApplies/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Page<EquipmentApplyVo>> getMyApplies(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PathVariable("pageNum") Integer pageNum,
            @PathVariable("pageSize") Integer pageSize
    ) {
        return Result.success(equipmentApplyService.getMyApplies(keyword, pageNum, pageSize));
    }

}
