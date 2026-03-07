package com.clj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 农资表
 * @TableName material
 */
@TableName(value ="material")
@Data
public class Material {
    /**
     * 农资ID
     */
    @TableId(type = IdType.AUTO)
    private Long materialId;

    /**
     * 农资名
     */
    private String materialName;

    /**
     * 农资类型ID
     */
    private Long materialTypeId;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 预警库存
     */
    private Integer warningStock;

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
        Material other = (Material) that;
        return (this.getMaterialId() == null ? other.getMaterialId() == null : this.getMaterialId().equals(other.getMaterialId()))
            && (this.getMaterialName() == null ? other.getMaterialName() == null : this.getMaterialName().equals(other.getMaterialName()))
            && (this.getMaterialTypeId() == null ? other.getMaterialTypeId() == null : this.getMaterialTypeId().equals(other.getMaterialTypeId()))
            && (this.getStock() == null ? other.getStock() == null : this.getStock().equals(other.getStock()))
            && (this.getWarningStock() == null ? other.getWarningStock() == null : this.getWarningStock().equals(other.getWarningStock()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getMaterialId() == null) ? 0 : getMaterialId().hashCode());
        result = prime * result + ((getMaterialName() == null) ? 0 : getMaterialName().hashCode());
        result = prime * result + ((getMaterialTypeId() == null) ? 0 : getMaterialTypeId().hashCode());
        result = prime * result + ((getStock() == null) ? 0 : getStock().hashCode());
        result = prime * result + ((getWarningStock() == null) ? 0 : getWarningStock().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", materialId=").append(materialId);
        sb.append(", materialName=").append(materialName);
        sb.append(", materialTypeId=").append(materialTypeId);
        sb.append(", stock=").append(stock);
        sb.append(", warningStock=").append(warningStock);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}