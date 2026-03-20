package com.clj.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PlantingRecordDto {
    private Long recordId;
    /**
     * 地块ID
     */
    private Long landId;

    /**
     * 农作物ID
     */
    private Long cropId;

    /**
     * 种植计划ID
     */
    private Long planId;

    /**
     * 种植日期
     */
    private Date plantingDate;

    /**
     * 期望收割日期
     */
    private Date expectedHarvestDate;

    /**
     * 实际收割日期
     */
    private Date actualHarvestDate;

    /**
     * 状态 0生长中 1已成熟
     */
    private Integer status;

    private BigDecimal outputQuantity;

}
