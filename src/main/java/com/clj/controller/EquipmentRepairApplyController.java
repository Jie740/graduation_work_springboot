package com.clj.controller;

import com.clj.common.result.Result;
import com.clj.domain.dto.EquipmentRepairApplyDto;
import com.clj.domain.vo.EquipmentRepairApplyVo;
import com.clj.service.EquipmentRepairApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/equipmentRepairApply")
public class EquipmentRepairApplyController {
    final EquipmentRepairApplyService equipmentRepairApplyService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Void> add(@RequestBody EquipmentRepairApplyDto equipmentRepairApplyDto){
        equipmentRepairApplyService.add(equipmentRepairApplyDto);
        return Result.success();
    }

    // 根据设备记录ID、申请人姓名和电话查询报修申请
    @GetMapping("/getByRecordId")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<EquipmentRepairApplyVo> getByRecordId(
            @RequestParam("recordId") Long recordId,
            @RequestParam("applicantName") String applicantName,
            @RequestParam("phone") String phone
    ) {
        return Result.success(equipmentRepairApplyService.getRepairApplyByRecordId(recordId, applicantName, phone));
    }
}
