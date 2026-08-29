package com.clj.ai.dto;

import lombok.Data;

import java.util.List;

/**
 * RAG 向量检索请求 DTO
 */
@Data
public class RagSearchRequestDto {

    /**
     * 知识库ID（单库检索时使用，与 knowledgeBaseIds 二者取并集）
     */
    private Long knowledgeBaseId;

    /**
     * 知识库ID列表（多库检索时使用）；为空表示不限定知识库
     */
    private List<Long> knowledgeBaseIds;

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
    private Double minScore = 0.70;
}
