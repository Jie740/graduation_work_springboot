package com.clj.controller;

import com.clj.domain.Material;
import com.clj.service.MaterialService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/material")
public class MaterialController {
    final MaterialService materialService;
    @PostMapping("/add")
    public Result add(@RequestBody Material material){
        return materialService.add(material);
    }
    @DeleteMapping("/delete/{materialId}")
    public Result delete(@PathVariable("materialId") Long materialId){
        return materialService.delete(materialId);
    }
    @PutMapping("/update")
    public Result update(@RequestBody Material material){
        return materialService.updateMaterial(material);
    }

    //根据农资类型和农资名查询
    @GetMapping("/searchMaterialsByPage/{pageNum}/{pageSize}")
    public Result searchMaterialsByTypeAndName(Long typeId, String keyword
            , @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return materialService.searchMaterialsByPage(typeId,keyword, pageNum, pageSize);
    }

    @GetMapping("/getAll")
    public Result getAll() {
        return materialService.getAll();
    }

    @GetMapping("/getMaterialTypeById/{materialId}")
    public Result getMaterialById(@PathVariable("materialId") Long materialId) {
        return materialService.getMaterialTypeById(materialId);
    }
}
