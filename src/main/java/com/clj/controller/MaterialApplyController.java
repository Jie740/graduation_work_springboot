package com.clj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.common.result.Result;
import com.clj.domain.MaterialApply;
import com.clj.domain.dto.MaterialApplyDto;
import com.clj.domain.vo.MaterialApplyVo;
import com.clj.service.MaterialApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/materialApply")
public class MaterialApplyController {
    final MaterialApplyService materialApplyService;
    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> add(@RequestBody MaterialApplyDto materialApplyDto){
        materialApplyService.add(materialApplyDto);
        return Result.success();
    }
    @GetMapping("/getMaterialApplyByPage/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<MaterialApplyVo>> getMaterialApplyByPage(@PathVariable("pageNum") Integer pageNum,
                                         @PathVariable("pageSize") Integer pageSize){
        return Result.success(materialApplyService.getMaterialApplyByPage(pageNum, pageSize));
    }

    @GetMapping("/searchMaterialApplyByPage/{keyword}/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<MaterialApplyVo>> searchMaterialApplyByPage(@PathVariable("keyword") String keyword
            , @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize){
        return Result.success(materialApplyService.searchMaterialApplyByPage(keyword, pageNum, pageSize));
    }

    @DeleteMapping("/delete/{applyId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> delete(@PathVariable("applyId") Long applyId){
        materialApplyService.delete(applyId);
        return Result.success();
    }

    @PutMapping("/update/{applyId}/{status}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> update(@PathVariable("applyId") Long applyId,
                         @PathVariable("status") Integer status){
        materialApplyService.updateMaterialApplyStatus(applyId, status);
        return Result.success();
    }
    @GetMapping("/getMaterialApplyById/{applyId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<MaterialApplyVo> getById(@PathVariable("applyId") Long applyId){
        return Result.success(materialApplyService.getMaterialApplyVoById(applyId));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> updateApply(@RequestBody MaterialApplyDto materialApplyDto){
        materialApplyService.updateApply(materialApplyDto);
        return Result.success();
    }

    // 根据用户ID分页查询我的农资申请
    @GetMapping("/getMyApplies/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<MaterialApplyVo>> getMyApplies(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PathVariable("pageNum") Integer pageNum,
            @PathVariable("pageSize") Integer pageSize
    ) {
        return Result.success(materialApplyService.getMyApplies(keyword, pageNum, pageSize));
    }
}
