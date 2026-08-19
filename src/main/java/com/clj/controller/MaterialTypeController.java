package com.clj.controller;

import com.clj.common.result.Result;
import com.clj.domain.MaterialType;
import com.clj.service.MaterialTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/materialType")
public class MaterialTypeController {
    final MaterialTypeService materialTypeService;
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> add(String typeName){
        materialTypeService.add(typeName);
        return Result.success();
    }
    @DeleteMapping("/delete/{materialTypeId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> delete(@PathVariable ("materialTypeId") Long materialTypeId){
        materialTypeService.delete(materialTypeId);
        return Result.success();
    }
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> update(@RequestBody MaterialType materialType){
        materialTypeService.updateMaterialType(materialType);
        return Result.success();
    }
    @GetMapping("/getAll")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<List<MaterialType>> getAll(){
        return Result.success(materialTypeService.getAll());
    }

}
