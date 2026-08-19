package com.clj.ai.controller;

import com.clj.ai.service.AiRagDocumentService;
import com.clj.ai.vo.DocumentUploadResultVo;
import com.clj.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * RAG 文档管理控制器
 */
@RestController
@RequestMapping("/ai/rag/document")
@RequiredArgsConstructor
public class AiRagDocumentController {

    private final AiRagDocumentService documentService;

    /**
     * 上传文档
     *
     * @param file 文件
     * @param knowledgeBaseId 知识库ID
     * @return 上传结果
     */
    @PostMapping("/upload")
    public Result<DocumentUploadResultVo> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("knowledgeBaseId") Long knowledgeBaseId) {
        DocumentUploadResultVo result = documentService.uploadDocument(file, knowledgeBaseId);
        return Result.success(result);
    }

    /**
     * 删除文档（逻辑删除）
     *
     * @param documentId 文档ID
     * @return 删除结果
     */
    @DeleteMapping("/{documentId}")
    public Result<Void> deleteDocument(@PathVariable Long documentId) {
        documentService.deleteDocument(documentId);
        return Result.success();
    }

    /**
     * 重新索引文档
     *
     * @param documentId 文档ID
     * @return 重新索引结果
     */
    @PostMapping("/{documentId}/reindex")
    public Result<Void> reindexDocument(@PathVariable Long documentId) {
        documentService.reindexDocument(documentId);
        return Result.success();
    }
}
