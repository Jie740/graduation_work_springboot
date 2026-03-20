package com.clj.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PlantingPlanAdjustDetailVo {
    private Long adjustId;
    private String planName;
    private String landName;
    private String landLocation;
    private BigDecimal landArea;
    private String cropName;
    private String creator;
    private BigDecimal expectedOutput;
    private Date startTime;
    private Date endTime;
    private String reason;
    private Date applyTime;
    private Integer status;
}
