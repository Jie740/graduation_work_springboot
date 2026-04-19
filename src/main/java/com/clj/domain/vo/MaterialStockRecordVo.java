package com.clj.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class MaterialStockRecordVo {
    private Long stockRecordId;
    private String materialName;
    private String type;
    private Integer recordType;
    private Integer quantity;
    private Date createTime;
}
