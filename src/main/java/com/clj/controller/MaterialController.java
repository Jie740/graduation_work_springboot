package com.clj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.common.result.Result;
import com.clj.domain.Material;
import com.clj.domain.vo.MaterialVo;
import com.clj.service.MaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/material")
public class MaterialController {
    final MaterialService materialService;
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<String> add(@RequestBody Material material){
        return Result.success(materialService.add(material));
    }
    @DeleteMapping("/delete/{materialId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> delete(@PathVariable("materialId") Long materialId){
        materialService.delete(materialId);
        return Result.success();
    }
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> update(@RequestBody Material material){
        materialService.updateMaterial(material);
        return Result.success();
    }

    //根据农资类型和农资名查询
    @GetMapping("/searchMaterialsByPage/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<MaterialVo>> searchMaterialsByTypeAndName(Long typeId, String keyword
            , @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize) {
        return Result.success(materialService.searchMaterialsByPage(typeId,keyword, pageNum, pageSize));
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<List<Material>> getAll() {
        return Result.success(materialService.getAll());
    }

    @GetMapping("/getMaterialTypeById/{materialId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Map<String, String>> getMaterialById(@PathVariable("materialId") Long materialId) {
        return Result.success(materialService.getMaterialTypeById(materialId));
    }
}
