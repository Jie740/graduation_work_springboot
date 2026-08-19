package com.clj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.common.exception.BusinessException;
import com.clj.common.result.Result;
import com.clj.domain.dto.MatureCropUpdateDto;
import com.clj.domain.vo.MatureCropStatisticsVo;
import com.clj.domain.vo.MatureCropVo;
import com.clj.service.MatureCropService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matureCrop")
public class MatureCropController {
    final MatureCropService matureCropService;


    @GetMapping("/getMatureCropsByPage/{pageNum}/{pageSize}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Page<MatureCropVo>> getMatureCropsByPage(@PathVariable("pageNum") Integer pageNum,
                                       @PathVariable("pageSize") Integer pageSize){
        return Result.success(matureCropService.getMatureCropsByPage(pageNum, pageSize));
    }



    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> update(@RequestBody MatureCropUpdateDto matureCropUpdateDto){
        matureCropService.updateMatureCrop(matureCropUpdateDto);
        return Result.success();
    }

    @DeleteMapping("/delete/{matureCropId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Void> delete(@PathVariable("matureCropId") Long id){
        if (!matureCropService.removeById(id)) {
            throw new BusinessException("删除失败");
        }
        return Result.success();
    }

    /**
     * 获取成熟作物统计数据
     * @param landId 地块ID（可选）
     * @param startDate 开始日期（可选），格式 "YYYY-MM-DD"
     * @param endDate 结束日期（可选），格式 "YYYY-MM-DD"
     * @return 统计数据
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<MatureCropStatisticsVo> getStatistics(
            @RequestParam(value = "landId", required = false) Long landId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate
    ){
        return Result.success(matureCropService.getStatistics(landId, startDate, endDate));
    }

    @GetMapping("/getOutputQuantity/{recordId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    public Result<Map<String, BigDecimal>> getOutputQuantity(@PathVariable("recordId") Long recordId){
        return Result.success(matureCropService.getOutputQuantity(recordId));
    }

}
