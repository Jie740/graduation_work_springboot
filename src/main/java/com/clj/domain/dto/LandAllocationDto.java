package com.clj.domain.dto;

import lombok.Data;

import java.util.Date;

@Data
public class LandAllocationDto {
    private Long allocationId;
    private Long landId;
    private String contractorName;
    private String phone;
    private Date startDate;
    private Date endDate;
}
