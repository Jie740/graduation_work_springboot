package com.clj.service;

import com.clj.domain.MatureCrop;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.utils.Result;

/**
* @author ajie
* @description 针对表【mature_crop(成熟作物表)】的数据库操作Service
* @createDate 2026-03-02 20:08:07
*/
public interface MatureCropService extends IService<MatureCrop> {

    Result add(MatureCrop matureCrop);

    Result getMatureCropsByPage(Integer pageNum, Integer pageSize);

    /**
     * 获取成熟作物统计数据
     * @param landId 地块ID（可选）
     * @param startDate 开始日期（可选），格式 "YYYY-MM-DD"
     * @param endDate 结束日期（可选），格式 "YYYY-MM-DD"
     * @return 统计数据
     */
    Result getStatistics(Long landId, String startDate, String endDate);

}
