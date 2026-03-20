package com.clj.controller;

import com.clj.domain.MaterialType;
import com.clj.service.MaterialTypeService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/materialType")
public class MaterialTypeController {
    final MaterialTypeService materialTypeService;
    @PostMapping("/add")
    public Result add(String typeName){
        return materialTypeService.add(typeName);
    }
    @DeleteMapping("/delete/{materialTypeId}")
    public Result delete(@PathVariable ("materialTypeId") Long materialTypeId){
        return materialTypeService.delete(materialTypeId);
    }
    @PutMapping("/update")
    public Result update(@RequestBody MaterialType materialType){
        return materialTypeService.updateMaterialType(materialType);
    }
    @GetMapping("/getAll")
    public Result getAll(){
        return materialTypeService.getAll();
    }

}
