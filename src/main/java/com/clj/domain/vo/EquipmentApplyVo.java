package com.clj.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class EquipmentApplyVo {
    private Long applyId;
    private String equipmentName;
    private String applicant;
    private String phone;
    private Date applyTime;
    private Integer status;
}
