package com.clj.ai.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * AI知识库表
 * @TableName ai_knowledge_base
 */
@TableName(value ="ai_knowledge_base")
@Data
public class AiKnowledgeBase {
    /**
     * 知识库ID
     */
    @TableId
    private Long id;

    /**
     * 知识库名称
     */
    private String name;

    /**
     * 知识库描述
     */
    private String description;

    /**
     * 状态：1启用 0禁用
     */
    private Integer status;

    /**
     * 知识库文档数量
     */
    private Integer documentCount;

    /**
     * 创建时间
     */
    private Date crtim;

    /**
     * 创建人ID
     */
    private Long cruid;

    /**
     * 更新时间
     */
    private Date uptim;

    /**
     * 更新人ID
     */
    private Long upuid;

    /**
     * 是否删除：false否 true是
     */
    private Boolean isDeleted;

    /**
     * 删除时间
     */
    private Date deletedAt;

    /**
     * 删除人ID
     */
    private Long deletedBy;

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
        AiKnowledgeBase other = (AiKnowledgeBase) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getDocumentCount() == null ? other.getDocumentCount() == null : this.getDocumentCount().equals(other.getDocumentCount()))
            && (this.getCrtim() == null ? other.getCrtim() == null : this.getCrtim().equals(other.getCrtim()))
            && (this.getCruid() == null ? other.getCruid() == null : this.getCruid().equals(other.getCruid()))
            && (this.getUptim() == null ? other.getUptim() == null : this.getUptim().equals(other.getUptim()))
            && (this.getUpuid() == null ? other.getUpuid() == null : this.getUpuid().equals(other.getUpuid()))
            && (this.getIsDeleted() == null ? other.getIsDeleted() == null : this.getIsDeleted().equals(other.getIsDeleted()))
            && (this.getDeletedAt() == null ? other.getDeletedAt() == null : this.getDeletedAt().equals(other.getDeletedAt()))
            && (this.getDeletedBy() == null ? other.getDeletedBy() == null : this.getDeletedBy().equals(other.getDeletedBy()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getDocumentCount() == null) ? 0 : getDocumentCount().hashCode());
        result = prime * result + ((getCrtim() == null) ? 0 : getCrtim().hashCode());
        result = prime * result + ((getCruid() == null) ? 0 : getCruid().hashCode());
        result = prime * result + ((getUptim() == null) ? 0 : getUptim().hashCode());
        result = prime * result + ((getUpuid() == null) ? 0 : getUpuid().hashCode());
        result = prime * result + ((getIsDeleted() == null) ? 0 : getIsDeleted().hashCode());
        result = prime * result + ((getDeletedAt() == null) ? 0 : getDeletedAt().hashCode());
        result = prime * result + ((getDeletedBy() == null) ? 0 : getDeletedBy().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", description=").append(description);
        sb.append(", status=").append(status);
        sb.append(", documentCount=").append(documentCount);
        sb.append(", crtim=").append(crtim);
        sb.append(", cruid=").append(cruid);
        sb.append(", uptim=").append(uptim);
        sb.append(", upuid=").append(upuid);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", deletedAt=").append(deletedAt);
        sb.append(", deletedBy=").append(deletedBy);
        sb.append("]");
        return sb.toString();
    }
}