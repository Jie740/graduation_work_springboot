package com.clj.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 加工记录表
 * @TableName processing_record
 */
@TableName(value ="processing_record")
@Data
public class ProcessingRecord {
    /**
     * 加工记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long processingId;

    /**
     * 成熟作物ID
     */
    private Long matureCropId;

    /**
     * 数量
     */
    private BigDecimal quantity;

    /**
     * 加工方法
     */
    private String processingMethod;

    /**
     * 描述
     */
    private String description;

    /**
     * 加工开始日期
     */
    private Date startDate;

    /**
     * 加工结束日期
     */
    private Date endDate;

    /**
     * 状态
     */
    private String status;

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
        ProcessingRecord other = (ProcessingRecord) that;
        return (this.getProcessingId() == null ? other.getProcessingId() == null : this.getProcessingId().equals(other.getProcessingId()))
            && (this.getMatureCropId() == null ? other.getMatureCropId() == null : this.getMatureCropId().equals(other.getMatureCropId()))
            && (this.getQuantity() == null ? other.getQuantity() == null : this.getQuantity().equals(other.getQuantity()))
            && (this.getProcessingMethod() == null ? other.getProcessingMethod() == null : this.getProcessingMethod().equals(other.getProcessingMethod()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getStartDate() == null ? other.getStartDate() == null : this.getStartDate().equals(other.getStartDate()))
            && (this.getEndDate() == null ? other.getEndDate() == null : this.getEndDate().equals(other.getEndDate()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getProcessingId() == null) ? 0 : getProcessingId().hashCode());
        result = prime * result + ((getMatureCropId() == null) ? 0 : getMatureCropId().hashCode());
        result = prime * result + ((getQuantity() == null) ? 0 : getQuantity().hashCode());
        result = prime * result + ((getProcessingMethod() == null) ? 0 : getProcessingMethod().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getStartDate() == null) ? 0 : getStartDate().hashCode());
        result = prime * result + ((getEndDate() == null) ? 0 : getEndDate().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", processingId=").append(processingId);
        sb.append(", matureCropId=").append(matureCropId);
        sb.append(", quantity=").append(quantity);
        sb.append(", processingMethod=").append(processingMethod);
        sb.append(", description=").append(description);
        sb.append(", startDate=").append(startDate);
        sb.append(", endDate=").append(endDate);
        sb.append(", status=").append(status);
        sb.append("]");
        return sb.toString();
    }
}