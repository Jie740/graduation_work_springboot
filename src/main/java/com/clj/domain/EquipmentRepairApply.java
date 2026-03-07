package com.clj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 设备报修申请表
 * @TableName equipment_repair_apply
 */
@TableName(value ="equipment_repair_apply")
@Data
public class EquipmentRepairApply {
    /**
     * 申请ID
     */
    @TableId(type = IdType.AUTO)
    private Long applyId;

    /**
     * 申请人ID
     */
    private Long applicantId;

    /**
     * 设备ID
     */
    private Long equipmentId;

    /**
     * 故障描述
     */
    private String faultDescription;

    /**
     * 申请时间
     */
    private Date applyTime;

    /**
     * 状态
     */
    private String status;

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
        EquipmentRepairApply other = (EquipmentRepairApply) that;
        return (this.getApplyId() == null ? other.getApplyId() == null : this.getApplyId().equals(other.getApplyId()))
            && (this.getApplicantId() == null ? other.getApplicantId() == null : this.getApplicantId().equals(other.getApplicantId()))
            && (this.getEquipmentId() == null ? other.getEquipmentId() == null : this.getEquipmentId().equals(other.getEquipmentId()))
            && (this.getFaultDescription() == null ? other.getFaultDescription() == null : this.getFaultDescription().equals(other.getFaultDescription()))
            && (this.getApplyTime() == null ? other.getApplyTime() == null : this.getApplyTime().equals(other.getApplyTime()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getApplyId() == null) ? 0 : getApplyId().hashCode());
        result = prime * result + ((getApplicantId() == null) ? 0 : getApplicantId().hashCode());
        result = prime * result + ((getEquipmentId() == null) ? 0 : getEquipmentId().hashCode());
        result = prime * result + ((getFaultDescription() == null) ? 0 : getFaultDescription().hashCode());
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
        sb.append(", applicantId=").append(applicantId);
        sb.append(", equipmentId=").append(equipmentId);
        sb.append(", faultDescription=").append(faultDescription);
        sb.append(", applyTime=").append(applyTime);
        sb.append(", status=").append(status);
        sb.append("]");
        return sb.toString();
    }
}