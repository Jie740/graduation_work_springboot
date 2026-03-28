package com.clj.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 成熟作物统计数据 VO
 */
@Data
public class MatureCropStatisticsVo {
    
    /**
     * 总作物数量
     */
    private Long totalCrops;
    
    /**
     * 总产量
     */
    private BigDecimal totalOutput;
    
    /**
     * 总地块数
     */
    private Long totalLands;
    
    /**
     * 平均产量
     */
    private BigDecimal avgOutput;
    
    /**
     * 按作物分类的产量
     */
    private List<CropOutputDto> cropOutput;
    
    /**
     * 按地块分类的产量
     */
    private List<LandOutputDto> landOutput;
    
    /**
     * 趋势数据（按月）
     */
    private List<TrendDataDto> trendData;
    
    /**
     * 饼图数据
     */
    private List<PieDataDto> pieData;
    
    /**
     * 作物产量 DTO
     */
    @Data
    public static class CropOutputDto {
        private String cropName;
        private BigDecimal output;
    }
    
    /**
     * 地块产量 DTO
     */
    @Data
    public static class LandOutputDto {
        private String landName;
        private BigDecimal output;
    }
    
    /**
     * 趋势数据 DTO
     */
    @Data
    public static class TrendDataDto {
        private String month;
        private BigDecimal output;
    }
    
    /**
     * 饼图数据 DTO
     */
    @Data
    public static class PieDataDto {
        private String name;
        private BigDecimal value;
    }
}
