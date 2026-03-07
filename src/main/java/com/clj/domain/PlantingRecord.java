package com.clj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 地块种植记录表
 * @TableName planting_record
 */
@TableName(value ="planting_record")
@Data
public class PlantingRecord {
    /**
     * 种植记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long recordId;

    /**
     * 地块ID
     */
    private Long landId;

    /**
     * 农作物ID
     */
    private Long cropId;

    /**
     * 种植计划ID
     */
    private Long planId;

    /**
     * 种植日期
     */
    private Date plantingDate;

    /**
     * 期望收割日期
     */
    private Date expectedHarvestDate;

    /**
     * 实际收割日期
     */
    private Date actualHarvestDate;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;

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
        PlantingRecord other = (PlantingRecord) that;
        return (this.getRecordId() == null ? other.getRecordId() == null : this.getRecordId().equals(other.getRecordId()))
            && (this.getLandId() == null ? other.getLandId() == null : this.getLandId().equals(other.getLandId()))
            && (this.getCropId() == null ? other.getCropId() == null : this.getCropId().equals(other.getCropId()))
            && (this.getPlanId() == null ? other.getPlanId() == null : this.getPlanId().equals(other.getPlanId()))
            && (this.getPlantingDate() == null ? other.getPlantingDate() == null : this.getPlantingDate().equals(other.getPlantingDate()))
            && (this.getExpectedHarvestDate() == null ? other.getExpectedHarvestDate() == null : this.getExpectedHarvestDate().equals(other.getExpectedHarvestDate()))
            && (this.getActualHarvestDate() == null ? other.getActualHarvestDate() == null : this.getActualHarvestDate().equals(other.getActualHarvestDate()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getRecordId() == null) ? 0 : getRecordId().hashCode());
        result = prime * result + ((getLandId() == null) ? 0 : getLandId().hashCode());
        result = prime * result + ((getCropId() == null) ? 0 : getCropId().hashCode());
        result = prime * result + ((getPlanId() == null) ? 0 : getPlanId().hashCode());
        result = prime * result + ((getPlantingDate() == null) ? 0 : getPlantingDate().hashCode());
        result = prime * result + ((getExpectedHarvestDate() == null) ? 0 : getExpectedHarvestDate().hashCode());
        result = prime * result + ((getActualHarvestDate() == null) ? 0 : getActualHarvestDate().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", recordId=").append(recordId);
        sb.append(", landId=").append(landId);
        sb.append(", cropId=").append(cropId);
        sb.append(", planId=").append(planId);
        sb.append(", plantingDate=").append(plantingDate);
        sb.append(", expectedHarvestDate=").append(expectedHarvestDate);
        sb.append(", actualHarvestDate=").append(actualHarvestDate);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}