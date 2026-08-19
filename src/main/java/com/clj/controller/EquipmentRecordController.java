package com.clj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.common.result.Result;
import com.clj.domain.Equipment;
import com.clj.domain.dto.EquipmentRecordDto;
import com.clj.domain.vo.EquipmentRecordVo;
import com.clj.service.EquipmentRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/equipmentRecord")
public class EquipmentRecordController {
    final EquipmentRecordService equipmentRecordService;
    @DeleteMapping("/delete/{equipmentRecordId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Void> delete(@PathVariable("equipmentRecordId") Long equipmentRecordId) {
        equipmentRecordService.delete(equipmentRecordId);
        return Result.success();
    }
    @PutMapping("/updateStatus")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Void> update(@RequestBody EquipmentRecordDto equipmentRecordDto) {
        System.out.println(equipmentRecordDto);
        equipmentRecordService.updateStatus(equipmentRecordDto);
        return Result.success();
    }


    @GetMapping("/getByPage/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<EquipmentRecordVo>> getLandAllocationByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return Result.success(equipmentRecordService.getByPage(pageNum, pageSize));
    }
    @GetMapping("/searchByPage/{keyword}/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<EquipmentRecordVo>> searchLandAllocationInfoByPage(@PathVariable("keyword") String keyword,
                                                 @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return Result.success(equipmentRecordService.searchByPage(keyword, pageNum, pageSize));
    }

    @GetMapping("/getByUserId/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<Page<EquipmentRecordVo>> getByUserId(
            @RequestParam(value = "keyword",required = false) String keyword
            , @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize
    ) {
        return Result.success(equipmentRecordService.getByUserId(keyword,pageNum,pageSize));
    }

    //获取用户设备列表
    @GetMapping("/getMyEquipment")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
    public Result<List<Equipment>> getMyEquipment() {
        return Result.success(equipmentRecordService.getMyEquipment());
    }
}
