package com.clj.controller;

import com.clj.domain.EquipmentType;
import com.clj.service.EquipmentTypeService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/equipmentType")
public class EquipmentTypeController {
    final EquipmentTypeService equipmentTypeService;
    @PostMapping("/add")
    public Result add(String equipmentTypeName) {
        return equipmentTypeService.add(equipmentTypeName);
    }
    @DeleteMapping("/delete/{equipmentTypeId}")
    public Result delete(@PathVariable("equipmentTypeId") Long equipmentTypeId) {
        return equipmentTypeService.delete(equipmentTypeId);
    }
    @PutMapping("/update")
    public Result update(@RequestBody EquipmentType equipmentType) {
        return equipmentTypeService.updateEquipmentType(equipmentType);
    }
    @GetMapping("/getEquipmentTypes")
    public Result getEquipmentTypes() {
        return equipmentTypeService.getEquipmentTypes();
    }
}
