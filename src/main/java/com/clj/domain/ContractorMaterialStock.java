package com.clj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 承包人农资库存表
 * @TableName contractor_material_stock
 */
@TableName(value ="contractor_material_stock")
@Data
public class ContractorMaterialStock {
    /**
     * 承包人农资ID
     */
    @TableId(type = IdType.AUTO)
    private Long contractorMaterialId;

    /**
     * 农资ID
     */
    private Long materialId;

    /**
     * 用户ID
     */
    private Long userId;

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
        ContractorMaterialStock other = (ContractorMaterialStock) that;
        return (this.getContractorMaterialId() == null ? other.getContractorMaterialId() == null : this.getContractorMaterialId().equals(other.getContractorMaterialId()))
            && (this.getMaterialId() == null ? other.getMaterialId() == null : this.getMaterialId().equals(other.getMaterialId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getStock() == null ? other.getStock() == null : this.getStock().equals(other.getStock()))
            && (this.getWarningStock() == null ? other.getWarningStock() == null : this.getWarningStock().equals(other.getWarningStock()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getContractorMaterialId() == null) ? 0 : getContractorMaterialId().hashCode());
        result = prime * result + ((getMaterialId() == null) ? 0 : getMaterialId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
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
        sb.append(", contractorMaterialId=").append(contractorMaterialId);
        sb.append(", materialId=").append(materialId);
        sb.append(", userId=").append(userId);
        sb.append(", stock=").append(stock);
        sb.append(", warningStock=").append(warningStock);
        sb.append(", createTime=").append(createTime);
        sb.append("]");
        return sb.toString();
    }
}