package com.clj.controller;

import com.clj.domain.MaterialApply;
import com.clj.domain.dto.MaterialApplyDto;
import com.clj.domain.vo.MaterialApplyVo;
import com.clj.service.MaterialApplyService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/materialApply")
public class MaterialApplyController {
    final MaterialApplyService materialApplyService;
    @PostMapping("/add")
    public Result add(@RequestBody MaterialApplyDto materialApplyDto){
        return materialApplyService.add(materialApplyDto);
    }
    @GetMapping("/getMaterialApplyByPage/{pageNum}/{pageSize}")
    public Result getMaterialApplyByPage(@PathVariable("pageNum") Integer pageNum,
                                         @PathVariable("pageSize") Integer pageSize){
        return materialApplyService.getMaterialApplyByPage(pageNum, pageSize);
    }

    @GetMapping("/searchMaterialApplyByPage/{keyword}/{pageNum}/{pageSize}")
    public Result searchMaterialApplyByPage(@PathVariable("keyword") String keyword
            , @PathVariable("pageNum") Integer pageNum, @PathVariable("pageSize") Integer pageSize){
        return materialApplyService.searchMaterialApplyByPage(keyword, pageNum, pageSize);
    }

    @DeleteMapping("/delete/{applyId}")
    public Result delete(@PathVariable("applyId") Long applyId){
        return materialApplyService.delete(applyId);
    }

    @PutMapping("/update/{applyId}/{status}")
    public Result update(@PathVariable("applyId") Long applyId,
                         @PathVariable("status") Integer status){
        return materialApplyService.updateMaterialApplyStatus(applyId, status);
    }
    @GetMapping("/getMaterialApplyById/{applyId}")
    public MaterialApplyVo getById(@PathVariable("applyId") Long applyId){
        return materialApplyService.getMaterialApplyVoById(applyId);
    }

    @PutMapping("/update")
    public Result updateApply(@RequestBody MaterialApplyDto materialApplyDto){
        return materialApplyService.updateApply(materialApplyDto);
    }

    // 根据用户ID分页查询我的农资申请
    @GetMapping("/getMyApplies/{pageNum}/{pageSize}")
    public Result getMyApplies(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PathVariable("pageNum") Integer pageNum,
            @PathVariable("pageSize") Integer pageSize
    ) {
        return materialApplyService.getMyApplies(keyword, pageNum, pageSize);
    }
}
