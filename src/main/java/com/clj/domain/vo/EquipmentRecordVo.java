package com.clj.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class EquipmentRecordVo {
    /**
     * 设备记录ID
     */
    private Long recordId;

    /**
     * 设备ID
     */
//    private Long equipmentId;

    private String equipmentName;
    private String typeName;

    /**
     * 隶属人ID
     */
//    private Long ownerId;
    private String ownerName;
    private String ownerPhone;

    /**
     * 状态 0正在使用 1报修中
     */
    private Integer status;
}
