package com.clj.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.security.PrivilegedAction;
import java.util.Date;

@Data
public class EquipmentRepairApplyDto {

    /**
     * 申请人ID
     */
//    private Long applicantId;
    private String applicantName;
    private String applicantPhone;

    /**
     * 设备ID
     */
    private Long equipmentId;
//    private String equipmentName;

    /**
     * 故障描述
     */
    private String faultDescription;

    /**
     * 申请时间
     */
    private Date applyTime;
}
