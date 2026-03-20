package com.clj.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MatureCropVo {
    private Long matureCropId;

    private String landName;
    private String location;

    private String cropName;

    private BigDecimal outputQuantity;
    private Date harvestTime;
}
