package com.clj.ai.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * AI知识库文档表
 * @TableName ai_rag_document
 */
@TableName(value ="ai_rag_document")
@Data
public class AiRagDocument {
    /**
     * 文档ID
     */
    @TableId
    private Long id;

    /**
     * 知识库ID
     */
    private Long knowledgeBaseId;

    /**
     * 文档名称
     */
    private String name;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件大小，单位字节
     */
    private Long fileSize;

    /**
     * MinIO对象名称
     */
    private String objectName;

    /**
     * 文件访问地址
     */
    private String fileUrl;

    /**
     * 文档原始文本内容
     */
    private String content;

    /**
     * 状态：1正常 0禁用
     */
    private Integer status;

    /**
     * 处理状态：0待处理 1解析中 2向量化中 3完成 4失败
     */
    private Integer processStatus;

    /**
     * 处理失败信息
     */
    private String processMessage;

    /**
     * 文档Chunk数量
     */
    private Integer chunkCount;

    /**
     * 文档版本
     */
    private Integer version;

    /**
     * 文件SHA-256哈希值
     */
    private String fileHash;

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
        AiRagDocument other = (AiRagDocument) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getKnowledgeBaseId() == null ? other.getKnowledgeBaseId() == null : this.getKnowledgeBaseId().equals(other.getKnowledgeBaseId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getFileName() == null ? other.getFileName() == null : this.getFileName().equals(other.getFileName()))
            && (this.getFileType() == null ? other.getFileType() == null : this.getFileType().equals(other.getFileType()))
            && (this.getFileSize() == null ? other.getFileSize() == null : this.getFileSize().equals(other.getFileSize()))
            && (this.getObjectName() == null ? other.getObjectName() == null : this.getObjectName().equals(other.getObjectName()))
            && (this.getFileUrl() == null ? other.getFileUrl() == null : this.getFileUrl().equals(other.getFileUrl()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getProcessStatus() == null ? other.getProcessStatus() == null : this.getProcessStatus().equals(other.getProcessStatus()))
            && (this.getProcessMessage() == null ? other.getProcessMessage() == null : this.getProcessMessage().equals(other.getProcessMessage()))
            && (this.getChunkCount() == null ? other.getChunkCount() == null : this.getChunkCount().equals(other.getChunkCount()))
            && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()))
            && (this.getFileHash() == null ? other.getFileHash() == null : this.getFileHash().equals(other.getFileHash()))
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
        result = prime * result + ((getKnowledgeBaseId() == null) ? 0 : getKnowledgeBaseId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getFileName() == null) ? 0 : getFileName().hashCode());
        result = prime * result + ((getFileType() == null) ? 0 : getFileType().hashCode());
        result = prime * result + ((getFileSize() == null) ? 0 : getFileSize().hashCode());
        result = prime * result + ((getObjectName() == null) ? 0 : getObjectName().hashCode());
        result = prime * result + ((getFileUrl() == null) ? 0 : getFileUrl().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getProcessStatus() == null) ? 0 : getProcessStatus().hashCode());
        result = prime * result + ((getProcessMessage() == null) ? 0 : getProcessMessage().hashCode());
        result = prime * result + ((getChunkCount() == null) ? 0 : getChunkCount().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + ((getFileHash() == null) ? 0 : getFileHash().hashCode());
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
        sb.append(", knowledgeBaseId=").append(knowledgeBaseId);
        sb.append(", name=").append(name);
        sb.append(", fileName=").append(fileName);
        sb.append(", fileType=").append(fileType);
        sb.append(", fileSize=").append(fileSize);
        sb.append(", objectName=").append(objectName);
        sb.append(", fileUrl=").append(fileUrl);
        sb.append(", content=").append(content);
        sb.append(", status=").append(status);
        sb.append(", processStatus=").append(processStatus);
        sb.append(", processMessage=").append(processMessage);
        sb.append(", chunkCount=").append(chunkCount);
        sb.append(", version=").append(version);
        sb.append(", fileHash=").append(fileHash);
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