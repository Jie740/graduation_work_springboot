package com.clj.controller;

import com.clj.common.result.Result;
import com.clj.domain.EquipmentType;
import com.clj.service.EquipmentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/equipmentType")
public class EquipmentTypeController {
    final EquipmentTypeService equipmentTypeService;
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> add(String equipmentTypeName) {
        equipmentTypeService.add(equipmentTypeName);
        return Result.success();
    }
    @DeleteMapping("/delete/{equipmentTypeId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> delete(@PathVariable("equipmentTypeId") Long equipmentTypeId) {
        equipmentTypeService.delete(equipmentTypeId);
        return Result.success();
    }
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> update(@RequestBody EquipmentType equipmentType) {
        equipmentTypeService.updateEquipmentType(equipmentType);
        return Result.success();
    }
    @GetMapping("/getEquipmentTypes")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<List<EquipmentType>> getEquipmentTypes() {
        return Result.success(equipmentTypeService.getEquipmentTypes());
    }
}
