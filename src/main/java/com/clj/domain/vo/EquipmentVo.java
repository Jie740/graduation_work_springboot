package com.clj.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class EquipmentVo {
    /**
     * 设备ID
     */
    @TableId(type = IdType.AUTO)
    private Long equipmentId;

    /**
     * 设备名
     */
    private String equipmentName;

    private Long equipmentTypeId;
    /**
     * 设备类型
     */
    private String equipmentTypeName;

    /**
     * 状态
     */
    private Integer status;
}
