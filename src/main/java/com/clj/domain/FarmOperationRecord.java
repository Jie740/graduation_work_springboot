package com.clj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 农事活动记录表
 * @TableName farm_operation_record
 */
@TableName(value ="farm_operation_record")
@Data
public class FarmOperationRecord {
    /**
     * 农事活动记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long operationId;

    /**
     * 种植记录ID
     */
    private Long recordId;

    /**
     * 农事活动类型（灌溉、施肥、除虫等）
     */
    private String operationType;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 承包人农资ID
     */
    private Long contractorMaterialId;

    /**
     * 用量
     */
    private BigDecimal quantity;

    /**
     * 操作责任人ID
     */
    private Long operatorId;

    /**
     * 操作时间
     */
    private Date operationTime;

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
        FarmOperationRecord other = (FarmOperationRecord) that;
        return (this.getOperationId() == null ? other.getOperationId() == null : this.getOperationId().equals(other.getOperationId()))
            && (this.getRecordId() == null ? other.getRecordId() == null : this.getRecordId().equals(other.getRecordId()))
            && (this.getOperationType() == null ? other.getOperationType() == null : this.getOperationType().equals(other.getOperationType()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getContractorMaterialId() == null ? other.getContractorMaterialId() == null : this.getContractorMaterialId().equals(other.getContractorMaterialId()))
            && (this.getQuantity() == null ? other.getQuantity() == null : this.getQuantity().equals(other.getQuantity()))
            && (this.getOperatorId() == null ? other.getOperatorId() == null : this.getOperatorId().equals(other.getOperatorId()))
            && (this.getOperationTime() == null ? other.getOperationTime() == null : this.getOperationTime().equals(other.getOperationTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getOperationId() == null) ? 0 : getOperationId().hashCode());
        result = prime * result + ((getRecordId() == null) ? 0 : getRecordId().hashCode());
        result = prime * result + ((getOperationType() == null) ? 0 : getOperationType().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getContractorMaterialId() == null) ? 0 : getContractorMaterialId().hashCode());
        result = prime * result + ((getQuantity() == null) ? 0 : getQuantity().hashCode());
        result = prime * result + ((getOperatorId() == null) ? 0 : getOperatorId().hashCode());
        result = prime * result + ((getOperationTime() == null) ? 0 : getOperationTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", operationId=").append(operationId);
        sb.append(", recordId=").append(recordId);
        sb.append(", operationType=").append(operationType);
        sb.append(", description=").append(description);
        sb.append(", contractorMaterialId=").append(contractorMaterialId);
        sb.append(", quantity=").append(quantity);
        sb.append(", operatorId=").append(operatorId);
        sb.append(", operationTime=").append(operationTime);
        sb.append("]");
        return sb.toString();
    }
}