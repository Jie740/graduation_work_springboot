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
     * 土壤湿度
     */
    private BigDecimal soilMoisture;

    /**
     * 环境温度
     */
    private BigDecimal temperature;

    /**
     * 光照强度
     */
    private BigDecimal lightIntensity;

    /**
     * 空气湿度
     */
    private BigDecimal airHumidity;

    /**
     * 采集时间
     */
    private Date collectTime;

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
            && (this.getSoilMoisture() == null ? other.getSoilMoisture() == null : this.getSoilMoisture().equals(other.getSoilMoisture()))
            && (this.getTemperature() == null ? other.getTemperature() == null : this.getTemperature().equals(other.getTemperature()))
            && (this.getLightIntensity() == null ? other.getLightIntensity() == null : this.getLightIntensity().equals(other.getLightIntensity()))
            && (this.getAirHumidity() == null ? other.getAirHumidity() == null : this.getAirHumidity().equals(other.getAirHumidity()))
            && (this.getCollectTime() == null ? other.getCollectTime() == null : this.getCollectTime().equals(other.getCollectTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getGrowthId() == null) ? 0 : getGrowthId().hashCode());
        result = prime * result + ((getRecordId() == null) ? 0 : getRecordId().hashCode());
        result = prime * result + ((getSoilMoisture() == null) ? 0 : getSoilMoisture().hashCode());
        result = prime * result + ((getTemperature() == null) ? 0 : getTemperature().hashCode());
        result = prime * result + ((getLightIntensity() == null) ? 0 : getLightIntensity().hashCode());
        result = prime * result + ((getAirHumidity() == null) ? 0 : getAirHumidity().hashCode());
        result = prime * result + ((getCollectTime() == null) ? 0 : getCollectTime().hashCode());
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
        sb.append(", soilMoisture=").append(soilMoisture);
        sb.append(", temperature=").append(temperature);
        sb.append(", lightIntensity=").append(lightIntensity);
        sb.append(", airHumidity=").append(airHumidity);
        sb.append(", collectTime=").append(collectTime);
        sb.append("]");
        return sb.toString();
    }
}