package com.clj.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.clj.ai.domain.AiKnowledgeBase;
import com.clj.ai.domain.AiRagChunk;
import com.clj.ai.domain.AiRagDocument;
import com.clj.ai.mapper.AiRagChunkMapper;
import com.clj.ai.mapper.AiRagDocumentMapper;
import com.clj.ai.service.AiKnowledgeBaseService;
import com.clj.ai.service.AiRagDocumentService;
import com.clj.ai.service.AiRagIngestionService;
import com.clj.ai.service.MinioService;
import com.clj.ai.vo.DocumentUploadResultVo;
import com.clj.common.exception.BusinessException;
import com.clj.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * RAG 文档管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiRagDocumentServiceImpl implements AiRagDocumentService {

    private final AiKnowledgeBaseService aiKnowledgeBaseService;
    private final AiRagDocumentMapper documentMapper;
    private final AiRagChunkMapper chunkMapper;
    private final MinioService minioService;
    private final AiRagIngestionService ingestionService;

    // 支持的文件类型
    private static final List<String> SUPPORTED_FILE_TYPES = Arrays.asList(
            "pdf", "doc", "docx", "txt", "md"
    );

    // 文档处理状态常量
    private static final int STATUS_PENDING = 0;

    @Override
    @Transactional
    public DocumentUploadResultVo uploadDocument(MultipartFile file, Long knowledgeBaseId) {
//        先查询对应的知识库ID是否存在
        boolean exists = aiKnowledgeBaseService.lambdaQuery()
                .eq(AiKnowledgeBase::getId, knowledgeBaseId)
                .exists();
        if (!exists){
            throw new BusinessException("知识库不存在");
        }
        // 1. 校验文件
        validateFile(file);

        // 2. 提取文件信息
        String originalFilename = file.getOriginalFilename();
        String fileType = getFileType(originalFilename);
        Long userId = SecurityUtil.getUserId();

        // 3. 创建文档记录（先获取 documentId）
        AiRagDocument document = new AiRagDocument();
        document.setKnowledgeBaseId(knowledgeBaseId);
        document.setName(originalFilename);
        document.setFileName(originalFilename);
        document.setFileType(fileType);
        document.setFileSize(file.getSize());
        document.setStatus(1); // 正常状态
        document.setProcessStatus(STATUS_PENDING);
        document.setChunkCount(0);
        document.setVersion(1);
        document.setIsDeleted(false);
        document.setCrtim(new Date());
        document.setCruid(userId);
        document.setUptim(new Date());
        document.setUpuid(userId);

        documentMapper.insert(document);
        Long documentId = document.getId();

        // 4. 构建 MinIO 对象路径: rag/{knowledgeBaseId}/{documentId}/{原始文件名}
        String objectName = String.format("rag/%d/%d/%s", knowledgeBaseId, documentId, originalFilename);

        // 5. 上传到 MinIO
        String fileUrl = minioService.uploadRagDocument(file, objectName);

        // 6. 更新文档的 objectName 和 fileUrl
        LambdaUpdateWrapper<AiRagDocument> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AiRagDocument::getId, documentId)
                .set(AiRagDocument::getObjectName, objectName)
                .set(AiRagDocument::getFileUrl, fileUrl);
        documentMapper.update(null, updateWrapper);

//        更新对应知识库的文档数量
        aiKnowledgeBaseService.lambdaUpdate()
                .setSql("document_count=document_count+1")
                .eq(AiKnowledgeBase::getId, knowledgeBaseId)
                        .update();
        // 7. 异步处理文档
        ingestionService.processDocumentAsync(documentId);

        // 8. 返回结果
        return DocumentUploadResultVo.builder()
                .documentId(documentId)
                .documentName(originalFilename)
                .fileUrl(fileUrl)
                .status("PENDING")
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDocument(Long documentId) {
        AiRagDocument document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }

        Long userId = SecurityUtil.getUserId();
        Date now = new Date();

        // 1. 逻辑删除文档
        LambdaUpdateWrapper<AiRagDocument> docUpdateWrapper = new LambdaUpdateWrapper<>();
        docUpdateWrapper.eq(AiRagDocument::getId, documentId)
                .set(AiRagDocument::getIsDeleted, true)
                .set(AiRagDocument::getDeletedAt, now)
                .set(AiRagDocument::getDeletedBy, userId);
        documentMapper.update(null, docUpdateWrapper);

        // 2. 逻辑删除关联的 chunks
        LambdaUpdateWrapper<AiRagChunk> chunkUpdateWrapper = new LambdaUpdateWrapper<>();
        chunkUpdateWrapper.eq(AiRagChunk::getDocumentId, documentId)
                .set(AiRagChunk::getIsDeleted, true)
                .set(AiRagChunk::getDeletedAt, now)
                .set(AiRagChunk::getDeletedBy, userId);
        chunkMapper.update(null, chunkUpdateWrapper);

        // 3. 删除 MinIO 文件
        if (document.getObjectName() != null) {
            try {
                minioService.deleteRagDocument(document.getObjectName());
            } catch (Exception e) {
                log.warn("删除 MinIO 文件失败: objectName={}", document.getObjectName(), e);
            }
        }

        log.info("文档已删除: documentId={}", documentId);
    }

    @Override
    public void reindexDocument(Long documentId) {
        AiRagDocument document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }

        // 异步重新索引
        ingestionService.reindexDocumentAsync(documentId);
    }

    /**
     * 校验文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BusinessException("文件名不能为空");
        }

        String fileType = getFileType(originalFilename);
        if (!SUPPORTED_FILE_TYPES.contains(fileType.toLowerCase())) {
            throw new BusinessException("不支持的文件类型: " + fileType + "，支持: " + SUPPORTED_FILE_TYPES);
        }
    }

    /**
     * 获取文件类型
     */
    private String getFileType(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
}
