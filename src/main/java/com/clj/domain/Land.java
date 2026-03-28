package com.clj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 地块信息表
 * @TableName land
 */
@TableName(value ="land")
@Data
public class Land {
    /**
     * 地块ID
     */
    @TableId(type = IdType.AUTO)
    private Long landId;

    /**
     * 地块名
     */
    private String landName;

    /**
     * 位置
     */
    private String location;

    /**
     * 面积（亩）
     */
    private BigDecimal area;

    /**
     * 土壤类型
     */
    private String soilType;

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
        Land other = (Land) that;
        return (this.getLandId() == null ? other.getLandId() == null : this.getLandId().equals(other.getLandId()))
            && (this.getLandName() == null ? other.getLandName() == null : this.getLandName().equals(other.getLandName()))
            && (this.getLocation() == null ? other.getLocation() == null : this.getLocation().equals(other.getLocation()))
            && (this.getArea() == null ? other.getArea() == null : this.getArea().equals(other.getArea()))
            && (this.getSoilType() == null ? other.getSoilType() == null : this.getSoilType().equals(other.getSoilType()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getLandId() == null) ? 0 : getLandId().hashCode());
        result = prime * result + ((getLandName() == null) ? 0 : getLandName().hashCode());
        result = prime * result + ((getLocation() == null) ? 0 : getLocation().hashCode());
        result = prime * result + ((getArea() == null) ? 0 : getArea().hashCode());
        result = prime * result + ((getSoilType() == null) ? 0 : getSoilType().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", landId=").append(landId);
        sb.append(", landName=").append(landName);
        sb.append(", location=").append(location);
        sb.append(", area=").append(area);
        sb.append(", soilType=").append(soilType);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}