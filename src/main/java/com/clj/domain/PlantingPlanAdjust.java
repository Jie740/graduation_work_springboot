package com.clj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 种植计划调整申请表
 * @TableName planting_plan_adjust
 */
@TableName(value ="planting_plan_adjust")
@Data
public class PlantingPlanAdjust {
    /**
     * 申请表ID
     */
    @TableId(type = IdType.AUTO)
    private Long adjustId;

    /**
     * 申请人ID
     */
    private Long applicantId;

    /**
     * 种植计划ID
     */
    private Long planId;

    /**
     * 
     */
    private Long landId;

    /**
     * 
     */
    private Long cropId;

    /**
     * 
     */
    private BigDecimal expectedOutput;

    /**
     * 
     */
    private Date startTime;

    /**
     * 
     */
    private Date endTime;

    /**
     * 调整原因描述
     */
    private String reason;

    /**
     * 申请时间
     */
    private Date applyTime;

    /**
     * 状态（0未审批 1 通过 2未通过）
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
        PlantingPlanAdjust other = (PlantingPlanAdjust) that;
        return (this.getAdjustId() == null ? other.getAdjustId() == null : this.getAdjustId().equals(other.getAdjustId()))
            && (this.getApplicantId() == null ? other.getApplicantId() == null : this.getApplicantId().equals(other.getApplicantId()))
            && (this.getPlanId() == null ? other.getPlanId() == null : this.getPlanId().equals(other.getPlanId()))
            && (this.getLandId() == null ? other.getLandId() == null : this.getLandId().equals(other.getLandId()))
            && (this.getCropId() == null ? other.getCropId() == null : this.getCropId().equals(other.getCropId()))
            && (this.getExpectedOutput() == null ? other.getExpectedOutput() == null : this.getExpectedOutput().equals(other.getExpectedOutput()))
            && (this.getStartTime() == null ? other.getStartTime() == null : this.getStartTime().equals(other.getStartTime()))
            && (this.getEndTime() == null ? other.getEndTime() == null : this.getEndTime().equals(other.getEndTime()))
            && (this.getReason() == null ? other.getReason() == null : this.getReason().equals(other.getReason()))
            && (this.getApplyTime() == null ? other.getApplyTime() == null : this.getApplyTime().equals(other.getApplyTime()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAdjustId() == null) ? 0 : getAdjustId().hashCode());
        result = prime * result + ((getApplicantId() == null) ? 0 : getApplicantId().hashCode());
        result = prime * result + ((getPlanId() == null) ? 0 : getPlanId().hashCode());
        result = prime * result + ((getLandId() == null) ? 0 : getLandId().hashCode());
        result = prime * result + ((getCropId() == null) ? 0 : getCropId().hashCode());
        result = prime * result + ((getExpectedOutput() == null) ? 0 : getExpectedOutput().hashCode());
        result = prime * result + ((getStartTime() == null) ? 0 : getStartTime().hashCode());
        result = prime * result + ((getEndTime() == null) ? 0 : getEndTime().hashCode());
        result = prime * result + ((getReason() == null) ? 0 : getReason().hashCode());
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
        sb.append(", adjustId=").append(adjustId);
        sb.append(", applicantId=").append(applicantId);
        sb.append(", planId=").append(planId);
        sb.append(", landId=").append(landId);
        sb.append(", cropId=").append(cropId);
        sb.append(", expectedOutput=").append(expectedOutput);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", reason=").append(reason);
        sb.append(", applyTime=").append(applyTime);
        sb.append(", status=").append(status);
        sb.append("]");
        return sb.toString();
    }
}