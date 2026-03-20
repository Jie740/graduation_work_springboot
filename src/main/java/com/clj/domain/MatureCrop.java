package com.clj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 成熟作物表
 * @TableName mature_crop
 */
@TableName(value ="mature_crop")
@Data
public class MatureCrop {
    /**
     * 成熟作物ID
     */
    @TableId(type = IdType.AUTO)
    private Long matureCropId;

    /**
     * 种植记录ID
     */
    private Long recordId;

    /**
     * 产出数量
     */
    private BigDecimal outputQuantity;

    /**
     * 收割时间
     */
    private Date harvestTime;

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
        MatureCrop other = (MatureCrop) that;
        return (this.getMatureCropId() == null ? other.getMatureCropId() == null : this.getMatureCropId().equals(other.getMatureCropId()))
            && (this.getRecordId() == null ? other.getRecordId() == null : this.getRecordId().equals(other.getRecordId()))
            && (this.getOutputQuantity() == null ? other.getOutputQuantity() == null : this.getOutputQuantity().equals(other.getOutputQuantity()))
            && (this.getHarvestTime() == null ? other.getHarvestTime() == null : this.getHarvestTime().equals(other.getHarvestTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getMatureCropId() == null) ? 0 : getMatureCropId().hashCode());
        result = prime * result + ((getRecordId() == null) ? 0 : getRecordId().hashCode());
        result = prime * result + ((getOutputQuantity() == null) ? 0 : getOutputQuantity().hashCode());
        result = prime * result + ((getHarvestTime() == null) ? 0 : getHarvestTime().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", matureCropId=").append(matureCropId);
        sb.append(", recordId=").append(recordId);
        sb.append(", outputQuantity=").append(outputQuantity);
        sb.append(", harvestTime=").append(harvestTime);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}