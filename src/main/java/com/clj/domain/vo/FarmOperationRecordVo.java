package com.clj.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FarmOperationRecordVo {
    private Long operationId;
    private String operationType;
    private String operatorName;
    private String phone;
    private Long materialId;
    private String materialName;
    private Integer quantity;
    private String description;
    private Date operationTime;
}
