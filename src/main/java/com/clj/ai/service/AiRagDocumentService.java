package com.clj.ai.service;

import com.clj.ai.vo.DocumentUploadResultVo;
import org.springframework.web.multipart.MultipartFile;

/**
 * RAG 文档管理服务
 */
public interface AiRagDocumentService {

    /**
     * 上传文档
     *
     * @param file 文件
     * @param knowledgeBaseId 知识库ID
     * @return 上传结果
     */
    DocumentUploadResultVo uploadDocument(MultipartFile file, Long knowledgeBaseId);

    /**
     * 删除文档（逻辑删除）
     *
     * @param documentId 文档ID
     */
    void deleteDocument(Long documentId);

    /**
     * 重新索引文档
     *
     * @param documentId 文档ID
     */
    void reindexDocument(Long documentId);
}
