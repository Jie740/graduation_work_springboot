package com.clj.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class LandAllocationVo {
    //分配Id
    private  Long allocationId;
//    /**
//     * 地块ID
//     */
//    private Long landId;
    private String landName;
    private String Area;

    /**
     * 承包人ID
     */
//    private Long contractorId;
    private String contractorName;
    private String Phone;

    /**
     * 开始日期
     */
    private Date startDate;

    /**
     * 结束日期
     */
    private Date endDate;
}
