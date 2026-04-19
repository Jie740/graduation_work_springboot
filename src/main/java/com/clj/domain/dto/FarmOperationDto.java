package com.clj.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date operationTime;
    private BigDecimal outputQuantity;
}
