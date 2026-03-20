package com.clj.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PlantingPlanDto {
    /**
     * 计划名
     */
    private String planName;

    /**
     * 地块名
     */
    private String landName;

    /**
     * 地块位置
     */
    private String landLocation;

    /**
     * 农作物名
     */
    private String cropName;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 期望产出
     */
    private BigDecimal expectedOutput;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

}
