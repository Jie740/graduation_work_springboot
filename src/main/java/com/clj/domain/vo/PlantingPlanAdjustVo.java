package com.clj.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class PlantingPlanAdjustVo {
    private Long adjustId;
    private String planName;
    private String applicant;
    private String phone;
    private String reason;
    private Date applyTime;
    private Integer status;
}
