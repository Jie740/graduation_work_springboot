package com.clj.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PlantingRecordVo {

    private Long recordId;
    private String landName;
    private String location;
    private BigDecimal area;
    private String cropName;
    private Date plantingDate;
    private Date expectedHarvestDate;
    private Date actualHarvestDate;
    private Date createTime;
    private Integer status;
}
