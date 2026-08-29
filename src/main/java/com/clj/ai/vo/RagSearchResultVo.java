package com.clj.ai.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * RAG 检索结果 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RagSearchResultVo {

    /**
     * 文档ID
     */
    private Long documentId;

    /**
     * Chunk ID
     */
    private Long chunkId;

    /**
     * Chunk 文本内容
     */
    private String content;

    /**
     * 所属知识库ID
     */
    private Long knowledgeBaseId;

    /**
     * 所属知识库名称
     */
    private String knowledgeBaseName;

    /**
     * 相似度得分
     */
    private Double score;

    /**
     * 元数据（包含 file_name, chunk_index 等）
     */
    private Map<String, Object> metadata;
}
