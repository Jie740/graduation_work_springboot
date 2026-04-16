package com.clj.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ContractorLandVo {
    /**
     * 地块ID
     */
    @TableId(type = IdType.AUTO)
    private Long landId;

    /**
     * 地块名
     */
    private String landName;

    /**
     * 位置
     */
    private String location;

    /**
     * 面积（亩）
     */
    private BigDecimal area;

    /**
     * 土壤类型
     */
    private String soilType;

    /**
     * 开始日期
     */
    private Date startDate;

    /**
     * 结束日期
     */
    private Date endDate;
}
