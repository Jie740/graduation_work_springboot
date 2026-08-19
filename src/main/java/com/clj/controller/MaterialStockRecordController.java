package com.clj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.common.result.Result;
import com.clj.domain.MaterialStockRecord;
import com.clj.domain.vo.MaterialStockRecordVo;
import com.clj.service.MaterialStockRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/materialStockRecord")
public class MaterialStockRecordController {

    private final MaterialStockRecordService materialStockRecordService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> add(@RequestBody MaterialStockRecord materialStockRecord) {
        materialStockRecordService.add(materialStockRecord);
        return Result.success();
    }

    @DeleteMapping("/delete/{stockRecordId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> delete(@PathVariable("stockRecordId") Long stockRecordId) {
        materialStockRecordService.delete(stockRecordId);
        return Result.success();
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> update(@RequestBody MaterialStockRecord materialStockRecord) {
        materialStockRecordService.update(materialStockRecord);
        return Result.success();
    }

    @GetMapping("/getByPage/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<MaterialStockRecordVo>> getByPage(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PathVariable("pageNum") Integer pageNum,
            @PathVariable("pageSize") Integer pageSize) {
        return Result.success(materialStockRecordService.getByPage(keyword, pageNum, pageSize));
    }
}
