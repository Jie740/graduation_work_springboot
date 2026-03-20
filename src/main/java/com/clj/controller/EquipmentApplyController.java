package com.clj.controller;

import com.clj.domain.EquipmentApply;
import com.clj.domain.dto.EquipmentApplyDto;
import com.clj.domain.vo.EquipmentApplyVo;
import com.clj.domain.vo.MaterialApplyVo;
import com.clj.service.EquipmentApplyService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/equipmentApply")
public class EquipmentApplyController {
    final EquipmentApplyService equipmentApplyService;
    @PostMapping("/add")
    public Result add(@RequestBody EquipmentApplyDto equipmentApplyDto){
        return equipmentApplyService.add(equipmentApplyDto);
    }
    @DeleteMapping("/delete/{applyId}")
    public Result delete(@PathVariable("applyId") Integer applyId){
        return equipmentApplyService.delete(applyId);
    }
    @PutMapping("/update")
    public Result update(@RequestBody EquipmentApplyDto equipmentApplyDto){
        return equipmentApplyService.updateApply(equipmentApplyDto);
    }

    @GetMapping("/getApplyByPage/{pageNum}/{pageSize}")
    public Result getApplyByPage(@PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize){
        return equipmentApplyService.getApplyByPage(pageNum, pageSize);
    }

    @GetMapping("/searchApplyByPage/{keyword}/{pageNum}/{pageSize}")
    public Result getApplyByPage(@PathVariable("keyword") String keyword, @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize){
        return equipmentApplyService.searchApplyByPage(keyword, pageNum, pageSize);
    }

    @PutMapping("/update/{applyId}/{status}")
    public Result update(@PathVariable("applyId") Long applyId,
                         @PathVariable("status") Integer status){
        return equipmentApplyService.updateApplyStatus(applyId, status);
    }
    @GetMapping("/getMaterialApplyById/{applyId}")
    public EquipmentApplyVo getById(@PathVariable("applyId") Long applyId){
        return equipmentApplyService.getApplyVoById(applyId);
    }

    @GetMapping("/getEquipmentNameAndTypeNameById/{applyId}")
    public Result getEquipmentNameAndTypeNameById(@PathVariable("applyId") Long applyId){
        return equipmentApplyService.getEquipmentNameAndTypeNameById(applyId);
    }

}
