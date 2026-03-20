package com.clj.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PlantingPlanAdjustDto {
    private String applicant;
    private String phone;
    private Long planId;
    private Long landId;
    private Long cropId;
    private BigDecimal expectedOutput;
    private Date startTime;
    private Date endTime;
    private String reason;
    private Date applyTime;
    private Integer status;
}
