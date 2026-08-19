package com.clj.ai.service;

/**
 * RAG 文档处理服务 - 负责文档解析、切块、向量化
 */
public interface AiRagIngestionService {

    /**
     * 异步处理文档：解析 → 切块 → 向量化 → 存储
     *
     * @param documentId 文档ID
     */
    void processDocumentAsync(Long documentId);

    /**
     * 重新索引文档：删除旧向量 → 重新处理
     *
     * @param documentId 文档ID
     */
    void reindexDocumentAsync(Long documentId);
}
