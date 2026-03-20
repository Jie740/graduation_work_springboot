package com.clj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 设备申请表
 * @TableName equipment_apply
 */
@TableName(value ="equipment_apply")
@Data
public class EquipmentApply {
    /**
     * 申请ID
     */
    @TableId(type = IdType.AUTO)
    private Long applyId;

    /**
     * 设备ID
     */
    private Long equipmentId;

    /**
     * 申请人ID
     */
    private Long applicantId;

    /**
     * 申请时间
     */
    private Date applyTime;

    /**
     * 状态 0未审核 1已通过 2未通过
     */
    private Integer status;

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
        EquipmentApply other = (EquipmentApply) that;
        return (this.getApplyId() == null ? other.getApplyId() == null : this.getApplyId().equals(other.getApplyId()))
            && (this.getEquipmentId() == null ? other.getEquipmentId() == null : this.getEquipmentId().equals(other.getEquipmentId()))
            && (this.getApplicantId() == null ? other.getApplicantId() == null : this.getApplicantId().equals(other.getApplicantId()))
            && (this.getApplyTime() == null ? other.getApplyTime() == null : this.getApplyTime().equals(other.getApplyTime()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getApplyId() == null) ? 0 : getApplyId().hashCode());
        result = prime * result + ((getEquipmentId() == null) ? 0 : getEquipmentId().hashCode());
        result = prime * result + ((getApplicantId() == null) ? 0 : getApplicantId().hashCode());
        result = prime * result + ((getApplyTime() == null) ? 0 : getApplyTime().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", applyId=").append(applyId);
        sb.append(", equipmentId=").append(equipmentId);
        sb.append(", applicantId=").append(applicantId);
        sb.append(", applyTime=").append(applyTime);
        sb.append(", status=").append(status);
        sb.append("]");
        return sb.toString();
    }
}