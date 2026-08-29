package com.clj.ai.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.clj.ai.config.JsonbTypeHandler;
import java.util.Date;
import lombok.Data;

/**
 * AI知识库RAG文档分块及向量数据表
 * @TableName ai_rag_chunk
 */
@TableName(value ="ai_rag_chunk", autoResultMap = true)
@Data
public class AiRagChunk {
    /**
     * Chunk唯一ID
     */
    @TableId
    private Long id;

    /**
     * 所属文档ID，关联ai_rag_document.id
     */
    private Long documentId;

    /**
     * Chunk在原始文档中的顺序，从0开始
     */
    private Integer chunkIndex;

    /**
     * Chunk文本内容，用于语义检索和大模型上下文增强
     */
    private String text;

    /**
     * Chunk文本Token数量，用于统计文本长度及控制上下文大小
     */
    private Integer tokenCount;

    /**
     * 文本Embedding向量，用于语义相似度检索
     */
    private Object embedding;

    /**
     * 生成当前向量所使用的Embedding模型名称
     */
    private String embeddingModel;

    /**
     * Embedding模型版本，用于向量数据版本管理
     */
    private String embeddingVersion;

    /**
     * RAG元数据，用于记录文档页码、章节、标题、来源文件等信息（JSON字符串）
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private String metadata;

    /**
     * 是否参与RAG检索：true参与，false不参与
     */
    private Boolean isActive;

    /**
     * 创建时间
     */
    private Date crtim;

    /**
     * 创建人ID
     */
    private Long cruid;

    /**
     * 最后更新时间
     */
    private Date uptim;

    /**
     * 最后更新人ID
     */
    private Long upuid;

    /**
     * 是否软删除：false未删除，true已删除
     */
    private Boolean isDeleted;

    /**
     * 软删除时间
     */
    private Date deletedAt;

    /**
     * 执行软删除操作的用户ID
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
        AiRagChunk other = (AiRagChunk) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getDocumentId() == null ? other.getDocumentId() == null : this.getDocumentId().equals(other.getDocumentId()))
            && (this.getChunkIndex() == null ? other.getChunkIndex() == null : this.getChunkIndex().equals(other.getChunkIndex()))
            && (this.getText() == null ? other.getText() == null : this.getText().equals(other.getText()))
            && (this.getTokenCount() == null ? other.getTokenCount() == null : this.getTokenCount().equals(other.getTokenCount()))
            && (this.getEmbedding() == null ? other.getEmbedding() == null : this.getEmbedding().equals(other.getEmbedding()))
            && (this.getEmbeddingModel() == null ? other.getEmbeddingModel() == null : this.getEmbeddingModel().equals(other.getEmbeddingModel()))
            && (this.getEmbeddingVersion() == null ? other.getEmbeddingVersion() == null : this.getEmbeddingVersion().equals(other.getEmbeddingVersion()))
            && (this.getMetadata() == null ? other.getMetadata() == null : this.getMetadata().equals(other.getMetadata()))
            && (this.getIsActive() == null ? other.getIsActive() == null : this.getIsActive().equals(other.getIsActive()))
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
        result = prime * result + ((getDocumentId() == null) ? 0 : getDocumentId().hashCode());
        result = prime * result + ((getChunkIndex() == null) ? 0 : getChunkIndex().hashCode());
        result = prime * result + ((getText() == null) ? 0 : getText().hashCode());
        result = prime * result + ((getTokenCount() == null) ? 0 : getTokenCount().hashCode());
        result = prime * result + ((getEmbedding() == null) ? 0 : getEmbedding().hashCode());
        result = prime * result + ((getEmbeddingModel() == null) ? 0 : getEmbeddingModel().hashCode());
        result = prime * result + ((getEmbeddingVersion() == null) ? 0 : getEmbeddingVersion().hashCode());
        result = prime * result + ((getMetadata() == null) ? 0 : getMetadata().hashCode());
        result = prime * result + ((getIsActive() == null) ? 0 : getIsActive().hashCode());
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
        sb.append(", documentId=").append(documentId);
        sb.append(", chunkIndex=").append(chunkIndex);
        sb.append(", text=").append(text);
        sb.append(", tokenCount=").append(tokenCount);
        sb.append(", embedding=").append(embedding);
        sb.append(", embeddingModel=").append(embeddingModel);
        sb.append(", embeddingVersion=").append(embeddingVersion);
        sb.append(", metadata=").append(metadata);
        sb.append(", isActive=").append(isActive);
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