package com.clj.controller;

import com.clj.domain.dto.EquipmentRepairApplyDto;
import com.clj.service.EquipmentRepairApplyService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/equipmentRepairApply")
public class EquipmentRepairApplyController {
    final EquipmentRepairApplyService equipmentRepairApplyService;

    @PostMapping("/add")
    public Result add(@RequestBody EquipmentRepairApplyDto equipmentRepairApplyDto){
        return equipmentRepairApplyService.add(equipmentRepairApplyDto);
    }
}
