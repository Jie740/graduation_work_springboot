package com.clj.controller;

import com.clj.domain.Equipment;
import com.clj.service.EquipmentService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/equipment")
public class EquipmentController {
    final EquipmentService equipmentService;
    @PostMapping("/addEquipment")
    public Result addEquipment(@RequestBody Equipment equipment) {
        return equipmentService.add(equipment);
    }
    @DeleteMapping("/deleteEquipment/{equipmentId}")
    public Result deleteEquipment(@PathVariable("equipmentId") Long equipmentId) {
        return equipmentService.delete(equipmentId);
    }
    @PutMapping("/updateEquipment")
    public Result updateEquipment(@RequestBody Equipment equipment) {
        return equipmentService.updateEquipment(equipment);
    }
    @GetMapping("/getEquipmentByPage/{pageNum}/{pageSize}")
    public Result getEquipmentByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return equipmentService.getEquipmentByPage(pageNum, pageSize);
    }

//    查询条件：设备名
    @GetMapping("/searchEquipmentByPage/{keyword}/{pageNum}/{pageSize}")
    public Result searchEquipmentByPage(@PathVariable("keyword") String keyword
            , @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return equipmentService.searchEquipmentByPage(keyword, pageNum, pageSize);
    }

    @GetMapping("/getAll")
    public Result getAll() {
        return Result.ok(equipmentService.list());
    }

    @GetMapping("/getEquipmentTypeNameById/{equipmentId}")
    public Result getEquipmentTypeNameById(@PathVariable("equipmentId") Long equipmentId) {
        return equipmentService.getEquipmentTypeNameById(equipmentId);
    }


}
