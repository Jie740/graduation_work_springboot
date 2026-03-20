package com.clj.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PlantingPlanVo {
    /**
     * 种植计划ID
     */
    private Long planId;

    /**
     * 地块ID
     */
    private Long landId;
    /**
     * 农作物ID
     */
    private Long cropId;
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
     * 地块面积
     */
    private BigDecimal landArea;
    /**
     * 农作物名
     */
    private String cropName;
    /**
     * 创建人
     */
    private String creator;
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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 状态
     */
    private Integer status;
}
