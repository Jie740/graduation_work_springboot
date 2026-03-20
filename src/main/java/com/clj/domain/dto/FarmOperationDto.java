package com.clj.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


@Data
public class FarmOperationDto {
    private Long operationId;
    private Long recordId;
    private String operationType;
    private String operatorName;
    private String phone;
    private Long materialId;
    private Integer quantity;
    private String description;
    private Date operationTime;
}
