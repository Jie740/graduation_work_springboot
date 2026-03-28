package com.clj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 农作物生长记录表
 * @TableName crop_growth_record
 */
@TableName(value ="crop_growth_record")
@Data
public class CropGrowthRecord {
    /**
     * 农作物生长记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long growthId;

    /**
     * 种植记录ID
     */
    private Long recordId;

    /**
     * 采集时间
     */
    private Date collectTime;

    /**
     * 温度
     */
    private BigDecimal temperature;

    /**
     * 湿度
     */
    private BigDecimal humidity;

    /**
     * 光照
     */
    private BigDecimal light;

    /**
     * ph值
     */
    private BigDecimal ph;

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
        CropGrowthRecord other = (CropGrowthRecord) that;
        return (this.getGrowthId() == null ? other.getGrowthId() == null : this.getGrowthId().equals(other.getGrowthId()))
            && (this.getRecordId() == null ? other.getRecordId() == null : this.getRecordId().equals(other.getRecordId()))
            && (this.getCollectTime() == null ? other.getCollectTime() == null : this.getCollectTime().equals(other.getCollectTime()))
            && (this.getTemperature() == null ? other.getTemperature() == null : this.getTemperature().equals(other.getTemperature()))
            && (this.getHumidity() == null ? other.getHumidity() == null : this.getHumidity().equals(other.getHumidity()))
            && (this.getLight() == null ? other.getLight() == null : this.getLight().equals(other.getLight()))
            && (this.getPh() == null ? other.getPh() == null : this.getPh().equals(other.getPh()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getGrowthId() == null) ? 0 : getGrowthId().hashCode());
        result = prime * result + ((getRecordId() == null) ? 0 : getRecordId().hashCode());
        result = prime * result + ((getCollectTime() == null) ? 0 : getCollectTime().hashCode());
        result = prime * result + ((getTemperature() == null) ? 0 : getTemperature().hashCode());
        result = prime * result + ((getHumidity() == null) ? 0 : getHumidity().hashCode());
        result = prime * result + ((getLight() == null) ? 0 : getLight().hashCode());
        result = prime * result + ((getPh() == null) ? 0 : getPh().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", growthId=").append(growthId);
        sb.append(", recordId=").append(recordId);
        sb.append(", collectTime=").append(collectTime);
        sb.append(", temperature=").append(temperature);
        sb.append(", humidity=").append(humidity);
        sb.append(", light=").append(light);
        sb.append(", ph=").append(ph);
        sb.append("]");
        return sb.toString();
    }
}