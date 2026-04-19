package com.clj.controller;

import com.clj.domain.MaterialStockRecord;
import com.clj.service.MaterialStockRecordService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/materialStockRecord")
public class MaterialStockRecordController {

    private final MaterialStockRecordService materialStockRecordService;

    @PostMapping("/add")
    public Result add(@RequestBody MaterialStockRecord materialStockRecord) {
        return materialStockRecordService.add(materialStockRecord);
    }

    @DeleteMapping("/delete/{stockRecordId}")
    public Result delete(@PathVariable("stockRecordId") Long stockRecordId) {
        return materialStockRecordService.delete(stockRecordId);
    }

    @PutMapping("/update")
    public Result update(@RequestBody MaterialStockRecord materialStockRecord) {
        return materialStockRecordService.update(materialStockRecord);
    }

    @GetMapping("/getByPage/{pageNum}/{pageSize}")
    public Result getByPage(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PathVariable("pageNum") Integer pageNum,
            @PathVariable("pageSize") Integer pageSize) {
        return materialStockRecordService.getByPage(keyword, pageNum, pageSize);
    }
}
