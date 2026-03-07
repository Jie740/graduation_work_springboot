package com.clj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 农资出入库记录表
 * @TableName material_stock_record
 */
@TableName(value ="material_stock_record")
@Data
public class MaterialStockRecord {
    /**
     * 农资出入库ID
     */
    @TableId(type = IdType.AUTO)
    private Long stockRecordId;

    /**
     * 农资ID
     */
    private Long materialId;

    /**
     * 类型（入库、出库）
     */
    private String type;

    /**
     * 数量
     */
    private Integer quantity;

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
        MaterialStockRecord other = (MaterialStockRecord) that;
        return (this.getStockRecordId() == null ? other.getStockRecordId() == null : this.getStockRecordId().equals(other.getStockRecordId()))
            && (this.getMaterialId() == null ? other.getMaterialId() == null : this.getMaterialId().equals(other.getMaterialId()))
            && (this.getType() == null ? other.getType() == null : this.getType().equals(other.getType()))
            && (this.getQuantity() == null ? other.getQuantity() == null : this.getQuantity().equals(other.getQuantity()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getStockRecordId() == null) ? 0 : getStockRecordId().hashCode());
        result = prime * result + ((getMaterialId() == null) ? 0 : getMaterialId().hashCode());
        result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
        result = prime * result + ((getQuantity() == null) ? 0 : getQuantity().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", stockRecordId=").append(stockRecordId);
        sb.append(", materialId=").append(materialId);
        sb.append(", type=").append(type);
        sb.append(", quantity=").append(quantity);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}