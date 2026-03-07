package com.clj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 设备类型表
 * @TableName equipment_type
 */
@TableName(value ="equipment_type")
@Data
public class EquipmentType {
    /**
     * 设备类型ID
     */
    @TableId(type = IdType.AUTO)
    private Long equipmentTypeId;

    /**
     * 设备类型名
     */
    private String equipmentTypeName;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        EquipmentType other = (EquipmentType) that;
        return (this.getEquipmentTypeId() == null ? other.getEquipmentTypeId() == null : this.getEquipmentTypeId().equals(other.getEquipmentTypeId()))
            && (this.getEquipmentTypeName() == null ? other.getEquipmentTypeName() == null : this.getEquipmentTypeName().equals(other.getEquipmentTypeName()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getEquipmentTypeId() == null) ? 0 : getEquipmentTypeId().hashCode());
        result = prime * result + ((getEquipmentTypeName() == null) ? 0 : getEquipmentTypeName().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", equipmentTypeId=").append(equipmentTypeId);
        sb.append(", equipmentTypeName=").append(equipmentTypeName);
        sb.append("]");
        return sb.toString();
    }
}