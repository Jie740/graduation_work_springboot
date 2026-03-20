package com.clj.domain.dto;

import lombok.Data;

import java.util.Date;

@Data
public class EquipmentApplyDto {
    private Long applyId;
    private Long equipmentId;
    private String applicant;
    private String phone;
    private Date applyTime;
    private Integer status;
}
