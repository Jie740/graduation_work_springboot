package com.clj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.common.result.Result;
import com.clj.domain.Equipment;
import com.clj.domain.vo.EquipmentVo;
import com.clj.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/equipment")
public class EquipmentController {
    final EquipmentService equipmentService;
    @PostMapping("/addEquipment")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> addEquipment(@RequestBody Equipment equipment) {
        equipmentService.add(equipment);
        return Result.success();
    }
    @DeleteMapping("/deleteEquipment/{equipmentId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> deleteEquipment(@PathVariable("equipmentId") Long equipmentId) {
        equipmentService.delete(equipmentId);
        return Result.success();
    }
    @PutMapping("/updateEquipment")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> updateEquipment(@RequestBody Equipment equipment) {
        equipmentService.updateEquipment(equipment);
        return Result.success();
    }
    @GetMapping("/getEquipmentByPage/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<EquipmentVo>> getEquipmentByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return Result.success(equipmentService.getEquipmentByPage(pageNum, pageSize));
    }

//    查询条件：设备名
    @GetMapping("/searchEquipmentByPage/{keyword}/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<EquipmentVo>> searchEquipmentByPage(@PathVariable("keyword") String keyword
            , @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return Result.success(equipmentService.searchEquipmentByPage(keyword, pageNum, pageSize));
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<List<Equipment>> getAll() {
        return Result.success(equipmentService.list());
    }

    @GetMapping("/getEquipmentTypeNameById/{equipmentId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Map<String, String>> getEquipmentTypeNameById(@PathVariable("equipmentId") Long equipmentId) {
        return Result.success(equipmentService.getEquipmentTypeNameById(equipmentId));
    }


}
