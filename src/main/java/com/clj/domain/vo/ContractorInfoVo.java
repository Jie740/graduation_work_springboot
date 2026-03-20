package com.clj.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ContractorInfoVo {
    private Long allocationId;
    private String name;
    private String phone;
    private Date startDate;
    private Date endDate;
}
