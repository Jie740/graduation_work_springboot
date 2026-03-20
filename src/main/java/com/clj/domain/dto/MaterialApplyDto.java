package com.clj.domain.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MaterialApplyDto {
    private Long applyId;
    /**
     * 农资ID
     */
    private Long materialId;
    private String applicant;
    private String phone;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 申请时间
     */
    private Date applyTime;
    private Integer status;
}
