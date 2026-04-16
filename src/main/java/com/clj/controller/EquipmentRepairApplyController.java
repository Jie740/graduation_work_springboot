package com.clj.controller;

import com.clj.domain.dto.EquipmentRepairApplyDto;
import com.clj.service.EquipmentRepairApplyService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/equipmentRepairApply")
public class EquipmentRepairApplyController {
    final EquipmentRepairApplyService equipmentRepairApplyService;

    @PostMapping("/add")
    public Result add(@RequestBody EquipmentRepairApplyDto equipmentRepairApplyDto){
        return equipmentRepairApplyService.add(equipmentRepairApplyDto);
    }

    // 根据设备记录ID、申请人姓名和电话查询报修申请
    @GetMapping("/getByRecordId")
    public Result getByRecordId(
            @RequestParam("recordId") Long recordId,
            @RequestParam("applicantName") String applicantName,
            @RequestParam("phone") String phone
    ) {
        return equipmentRepairApplyService.getRepairApplyByRecordId(recordId, applicantName, phone);
    }
}
