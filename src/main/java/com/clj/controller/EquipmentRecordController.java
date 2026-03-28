package com.clj.controller;

import com.clj.domain.dto.ContractorMaterialStockDto;
import com.clj.domain.dto.EquipmentRecordDto;
import com.clj.service.EquipmentRecordService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/equipmentRecord")
public class EquipmentRecordController {
    final EquipmentRecordService equipmentRecordService;
    @DeleteMapping("/delete/{equipmentRecordId}")
    public Result delete(@PathVariable("equipmentRecordId") Long equipmentRecordId) {
        return equipmentRecordService.delete(equipmentRecordId) ;
    }
    @PutMapping("/updateStatus")
    public Result update(@RequestBody EquipmentRecordDto equipmentRecordDto) {
        System.out.println(equipmentRecordDto);
        return equipmentRecordService.updateStatus(equipmentRecordDto);
    }


    @GetMapping("/getByPage/{pageNum}/{pageSize}")
    public Result getLandAllocationByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return equipmentRecordService.getByPage(pageNum, pageSize);
    }
    @GetMapping("/searchByPage/{keyword}/{pageNum}/{pageSize}")
    public Result searchLandAllocationInfoByPage(@PathVariable("keyword") String keyword,
                                                 @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return equipmentRecordService.searchByPage(keyword, pageNum, pageSize);
    }
}
