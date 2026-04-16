package com.clj.controller;

import com.clj.domain.MatureCrop;
import com.clj.service.MatureCropService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matureCrop")
public class MatureCropController {
    final MatureCropService matureCropService;


    @GetMapping("/getMatureCropsByPage/{pageNum}/{pageSize}")
    public Result getMatureCropsByPage(@PathVariable("pageNum") Integer pageNum,
                                       @PathVariable("pageSize") Integer pageSize){
        return matureCropService.getMatureCropsByPage(pageNum, pageSize);
    }

    @PutMapping("/update")
    public Result update(@RequestBody MatureCrop matureCrop){
        return matureCropService.updateById(matureCrop)?Result.ok():Result.error("更新失败");
    }

    @DeleteMapping("/delete/{matureCropId}")
    public Result delete(@PathVariable("matureCropId") Integer id){
        return matureCropService.removeById(id)?Result.ok():Result.error("删除失败");
    }

    /**
     * 获取成熟作物统计数据
     * @param landId 地块ID（可选）
     * @param startDate 开始日期（可选），格式 "YYYY-MM-DD"
     * @param endDate 结束日期（可选），格式 "YYYY-MM-DD"
     * @return 统计数据
     */
    @GetMapping("/statistics")
    public Result getStatistics(
            @RequestParam(value = "landId", required = false) Long landId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate
    ){
        return matureCropService.getStatistics(landId, startDate, endDate);
    }

    @GetMapping("/getOutputQuantity/{recordId}")
    public Result getOutputQuantity(@PathVariable("recordId") Long recordId){
        return matureCropService.getOutputQuantity(recordId);
    }

}
