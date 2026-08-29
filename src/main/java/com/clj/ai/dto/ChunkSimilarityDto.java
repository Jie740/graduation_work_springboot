package com.clj.ai.dto;

import lombok.Data;

/**
 * 向量相似度检索结果
 */
@Data
public class ChunkSimilarityDto {

    /**
     * 分块ID
     */
    private Long id;

    /**
     * 所属文档ID
     */
    private Long documentId;

    /**
     * 所属知识库ID
     */
    private Long knowledgeBaseId;

    /**
     * 所属知识库名称
     */
    private String knowledgeBaseName;

    /**
     * 分块内容
     */
    private String content;

    /**
     * 元数据（JSONB）
     */
    private String metadata;

    /**
     * 余弦相似度（1 - 余弦距离）
     */
    private Double similarity;
}
