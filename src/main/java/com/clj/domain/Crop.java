package com.clj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 农作物信息表
 * @TableName crop
 */
@TableName(value ="crop")
@Data
public class Crop {
    /**
     * 农作物ID
     */
    @TableId(type = IdType.AUTO)
    private Long cropId;

    /**
     * 农作物名
     */
    private String cropName;

    /**
     * 农作物类型
     */
    private String cropType;

    /**
     * 生长周期（天）
     */
    private Integer growthCycle;

    /**
     * 适应温度
     */
    private String suitableTemperature;

    /**
     * 适应湿度
     */
    private String suitableHumidity;

    /**
     * 描述
     */
    private String description;

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
        Crop other = (Crop) that;
        return (this.getCropId() == null ? other.getCropId() == null : this.getCropId().equals(other.getCropId()))
            && (this.getCropName() == null ? other.getCropName() == null : this.getCropName().equals(other.getCropName()))
            && (this.getCropType() == null ? other.getCropType() == null : this.getCropType().equals(other.getCropType()))
            && (this.getGrowthCycle() == null ? other.getGrowthCycle() == null : this.getGrowthCycle().equals(other.getGrowthCycle()))
            && (this.getSuitableTemperature() == null ? other.getSuitableTemperature() == null : this.getSuitableTemperature().equals(other.getSuitableTemperature()))
            && (this.getSuitableHumidity() == null ? other.getSuitableHumidity() == null : this.getSuitableHumidity().equals(other.getSuitableHumidity()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getCropId() == null) ? 0 : getCropId().hashCode());
        result = prime * result + ((getCropName() == null) ? 0 : getCropName().hashCode());
        result = prime * result + ((getCropType() == null) ? 0 : getCropType().hashCode());
        result = prime * result + ((getGrowthCycle() == null) ? 0 : getGrowthCycle().hashCode());
        result = prime * result + ((getSuitableTemperature() == null) ? 0 : getSuitableTemperature().hashCode());
        result = prime * result + ((getSuitableHumidity() == null) ? 0 : getSuitableHumidity().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", cropId=").append(cropId);
        sb.append(", cropName=").append(cropName);
        sb.append(", cropType=").append(cropType);
        sb.append(", growthCycle=").append(growthCycle);
        sb.append(", suitableTemperature=").append(suitableTemperature);
        sb.append(", suitableHumidity=").append(suitableHumidity);
        sb.append(", description=").append(description);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}