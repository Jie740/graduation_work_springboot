package com.clj.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class MaterialApplyVo {
    private Long applyId;
    private Long materialId;
    private String materialName;
    private String typeName;
    private Long applicantId;
    private String applicant;
    private String phone;
    private Integer quantity;
    private Date applyTime;
    private Integer status;
}
