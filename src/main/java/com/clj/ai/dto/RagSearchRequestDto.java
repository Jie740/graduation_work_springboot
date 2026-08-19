package com.clj.ai.dto;

import lombok.Data;

/**
 * RAG 向量检索请求 DTO
 */
@Data
public class RagSearchRequestDto {

    /**
     * 知识库ID
     */
    private Long knowledgeBaseId;

    /**
     * 查询文本
     */
    private String query;

    /**
     * 返回结果数量，默认5
     */
    private Integer topK = 5;

    /**
     * 最小相似度阈值，默认0.75
     */
    private Double minScore = 0.75;
}
