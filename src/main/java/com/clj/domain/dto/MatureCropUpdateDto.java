package com.clj.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MatureCropUpdateDto {
    private Long matureCropId;
    private BigDecimal outputQuantity;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date harvestTime;
}
