package com.clj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 种植计划表
 * @TableName planting_plan
 */
@TableName(value ="planting_plan")
@Data
public class PlantingPlan {
    /**
     * 种植计划ID
     */
    @TableId(type = IdType.AUTO)
    private Long planId;

    /**
     * 计划名
     */
    private String planName;

    /**
     * 地块ID
     */
    private Long landId;

    /**
     * 
     */
    private Long cropId;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 期望产出
     */
    private BigDecimal expectedOutput;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 状态（未发布、执行中、调整中、已完成、已终止）
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
        PlantingPlan other = (PlantingPlan) that;
        return (this.getPlanId() == null ? other.getPlanId() == null : this.getPlanId().equals(other.getPlanId()))
            && (this.getPlanName() == null ? other.getPlanName() == null : this.getPlanName().equals(other.getPlanName()))
            && (this.getLandId() == null ? other.getLandId() == null : this.getLandId().equals(other.getLandId()))
            && (this.getCropId() == null ? other.getCropId() == null : this.getCropId().equals(other.getCropId()))
            && (this.getCreatorId() == null ? other.getCreatorId() == null : this.getCreatorId().equals(other.getCreatorId()))
            && (this.getExpectedOutput() == null ? other.getExpectedOutput() == null : this.getExpectedOutput().equals(other.getExpectedOutput()))
            && (this.getStartTime() == null ? other.getStartTime() == null : this.getStartTime().equals(other.getStartTime()))
            && (this.getEndTime() == null ? other.getEndTime() == null : this.getEndTime().equals(other.getEndTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getPlanId() == null) ? 0 : getPlanId().hashCode());
        result = prime * result + ((getPlanName() == null) ? 0 : getPlanName().hashCode());
        result = prime * result + ((getLandId() == null) ? 0 : getLandId().hashCode());
        result = prime * result + ((getCropId() == null) ? 0 : getCropId().hashCode());
        result = prime * result + ((getCreatorId() == null) ? 0 : getCreatorId().hashCode());
        result = prime * result + ((getExpectedOutput() == null) ? 0 : getExpectedOutput().hashCode());
        result = prime * result + ((getStartTime() == null) ? 0 : getStartTime().hashCode());
        result = prime * result + ((getEndTime() == null) ? 0 : getEndTime().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", planId=").append(planId);
        sb.append(", planName=").append(planName);
        sb.append(", landId=").append(landId);
        sb.append(", cropId=").append(cropId);
        sb.append(", creatorId=").append(creatorId);
        sb.append(", expectedOutput=").append(expectedOutput);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", status=").append(status);
        sb.append("]");
        return sb.toString();
    }
}