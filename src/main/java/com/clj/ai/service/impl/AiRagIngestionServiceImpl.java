package com.clj.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.clj.ai.config.EmbeddingProperties;
import com.clj.ai.domain.AiRagChunk;
import com.clj.ai.domain.AiRagDocument;
import com.clj.ai.mapper.AiRagChunkMapper;
import com.clj.ai.mapper.AiRagDocumentMapper;
import com.clj.ai.service.AiRagIngestionService;
import com.clj.ai.service.MinioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgvector.PGvector;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RAG 文档处理服务实现
 * 使用 LangChain4j 原生能力进行文档解析、切块和向量化
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiRagIngestionServiceImpl implements AiRagIngestionService {

    private final ObjectMapper objectMapper;
    private final AiRagDocumentMapper documentMapper;
    private final AiRagChunkMapper chunkMapper;
    private final MinioService minioService;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingProperties embeddingProperties;

    // 文档处理状态常量
    private static final int STATUS_PENDING = 0;
    private static final int STATUS_PROCESSING = 1;
    private static final int STATUS_SUCCESS = 3;
    private static final int STATUS_FAILED = 4;

    /**
     * 章节标题匹配模式：支持「第X章/节/部分」和「1」「3.1」等数字编号标题
     */
    private static final Pattern HEADING_PATTERN = Pattern.compile(
            "^\\s*(第[一二三四五六七八九十百千0-9]+[章节部分]|[0-9]{1,2}(\\.[0-9]{1,2}){0,3}[ 　、.．].*)$",
            Pattern.MULTILINE);

    @Async
    @Override
    public void processDocumentAsync(Long documentId) {
        log.info("开始异步处理文档: documentId={}", documentId);

        AiRagDocument document = documentMapper.selectById(documentId);
        if (document == null) {
            log.error("文档不存在: documentId={}", documentId);
            return;
        }

        try {
            // 更新状态为处理中
            updateDocumentStatus(documentId, STATUS_PROCESSING, null);

            // 1. 从 MinIO 读取文档
            Document langChainDocument = loadDocument(document);

            // 2. 切块
            List<TextSegment> segments = splitDocument(langChainDocument, document);

            // 3. 生成向量
            List<Embedding> embeddings = generateEmbeddings(segments);

            // 4. 保存到向量数据库和业务数据库
            saveChunks(document, segments, embeddings);

            // 5. 更新文档状态为成功
            updateDocumentStatus(documentId, STATUS_SUCCESS, null);
            updateChunkCount(documentId, segments.size());

            log.info("文档处理完成: documentId={}, chunks={}", documentId, segments.size());

        } catch (Exception e) {
            log.error("文档处理失败: documentId={}", documentId, e);
            updateDocumentStatus(documentId, STATUS_FAILED, e.getMessage());
        }
    }

    @Async
    @Override
    public void reindexDocumentAsync(Long documentId) {
        log.info("开始重新索引文档: documentId={}", documentId);

        AiRagDocument document = documentMapper.selectById(documentId);
        if (document == null) {
            log.error("文档不存在: documentId={}", documentId);
            return;
        }

        try {
            // 1. 停用旧的 chunks
            deactivateChunks(documentId);

            // 2. 重新处理文档
            updateDocumentStatus(documentId, STATUS_PROCESSING, null);

            Document langChainDocument = loadDocument(document);
            List<TextSegment> segments = splitDocument(langChainDocument, document);
            List<Embedding> embeddings = generateEmbeddings(segments);
            saveChunks(document, segments, embeddings);

            // 3. 更新状态
            updateDocumentStatus(documentId, STATUS_SUCCESS, null);
            updateChunkCount(documentId, segments.size());

            log.info("文档重新索引完成: documentId={}, chunks={}", documentId, segments.size());

        } catch (Exception e) {
            log.error("文档重新索引失败: documentId={}", documentId, e);
            updateDocumentStatus(documentId, STATUS_FAILED, e.getMessage());
        }
    }

    private String cleanText(String text) {

        if (text == null) {
            return "";
        }

        // Unicode兼容字符转标准字符
        text = java.text.Normalizer.normalize(
                text,
                java.text.Normalizer.Form.NFKC
        );

        // 删除乱码字符
        text = text.replace("\uFFFD", "");

        // 删除零宽字符
        text = text.replaceAll(
                "[\\u200B\\u200C\\u200D\\uFEFF]",
                ""
        );

        // 多空格压缩
        text = text.replaceAll("[ \\t]+", " ");

        // 空行压缩
        text = text.replaceAll("\\n{3,}", "\n\n");

        return text.trim();
    }
    /**
     * 从 MinIO 加载文档
     */
    private Document loadDocument(AiRagDocument document) {

        try (InputStream inputStream =
                     minioService.readRagDocument(document.getObjectName())) {

            ApacheTikaDocumentParser parser =
                    new ApacheTikaDocumentParser();

            Document doc = parser.parse(inputStream);

            //数据清洗
            String cleanedText = cleanText(doc.text());

            return Document.from(
                    cleanedText,
                    doc.metadata()
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "文档解析失败: " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * 使用 LangChain4j 切块
     */
    private List<TextSegment> splitDocument(Document document, AiRagDocument ragDocument) {
        // 使用 RecursiveDocumentSplitter，maxSegmentSize=500 tokens, overlap=50 tokens
        var splitter = DocumentSplitters.recursive(500, 50);

        List<TextSegment> segments = splitter.split(document);

        // 全文用于定位每个 segment 的偏移，从而推算页码与所属章节
        String fullText = document.text();
        String documentTitle = extractTitle(document, ragDocument);

        int searchFrom = 0;
        // 为每个 segment 添加 metadata（页码、章节、标题、来源文件等）
        for (int i = 0; i < segments.size(); i++) {
            TextSegment segment = segments.get(i);
            Map<String, Object> metadata = new HashMap<>(segment.metadata().toMap());

            // 定位 segment 在全文中的起始位置（考虑切块重叠，从上一位置向后查找）
            int offset = fullText.indexOf(segment.text(), searchFrom);
            if (offset < 0) {
                offset = searchFrom;
            }
            searchFrom = offset;

            // 页码：Tika 以 \f（换页符）分隔页面，统计之前的换页符数量 + 1
            int pageNumber = countPages(fullText, offset);

            // 章节标题：取该位置之前最近出现的标题行
            String section = extractSection(fullText, offset);

            metadata.put("document_id", ragDocument.getId());
            metadata.put("knowledge_base_id", ragDocument.getKnowledgeBaseId());
            metadata.put("file_name", ragDocument.getFileName());
            metadata.put("source", ragDocument.getObjectName());
            metadata.put("chunk_index", i);
            metadata.put("page_number", pageNumber);
            metadata.put("section", section);
            metadata.put("title", documentTitle);

            segments.set(i, TextSegment.from(segment.text(), dev.langchain4j.data.document.Metadata.from(metadata)));
        }

        return segments;
    }

    /**
     * 统计 offset 之前的换页符数量，得到页码（从 1 开始）
     */
    private int countPages(String fullText, int offset) {
        int pages = 1;
        for (int i = 0; i < offset && i < fullText.length(); i++) {
            if (fullText.charAt(i) == '\f') {
                pages++;
            }
        }
        return pages;
    }

    /**
     * 提取 offset 之前最近出现的章节标题行
     */
    private String extractSection(String fullText, int offset) {
        String before = fullText.substring(0, Math.min(offset, fullText.length()));
        Matcher matcher = HEADING_PATTERN.matcher(before);
        String lastHeading = null;
        while (matcher.find()) {
            lastHeading = matcher.group().trim();
        }
        return lastHeading == null ? "" : lastHeading;
    }

    /**
     * 提取文档标题：优先使用 Tika 解析出的标题，其次取首行文本，最后回退到文件名
     */
    private String extractTitle(Document document, AiRagDocument ragDocument) {
        String title = document.metadata().getString("title");
        if (title != null && !title.isBlank()) {
            return title.trim();
        }
        String text = document.text();
        if (text != null && !text.isBlank()) {
            String firstLine = text.split("\\r?\\n")[0].trim();
            if (!firstLine.isEmpty() && firstLine.length() <= 100) {
                return firstLine;
            }
        }
        return ragDocument.getFileName();
    }

    /**
     * 生成向量
     */
    private List<Embedding> generateEmbeddings(List<TextSegment> segments) {
        return embeddingModel.embedAll(segments).content();
    }

    /**
     * 保存 chunks 到数据库（文本与向量统一存储在 ai_rag_chunk 表）
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveChunks(AiRagDocument document, List<TextSegment> segments, List<Embedding> embeddings) {
        for (int i = 0; i < segments.size(); i++) {
            TextSegment segment = segments.get(i);
            Embedding embedding = embeddings.get(i);

            // 保存到业务数据库（含向量）
            AiRagChunk chunk = new AiRagChunk();
            chunk.setDocumentId(document.getId());
            chunk.setChunkIndex(i);
            chunk.setText(segment.text());
            chunk.setTokenCount(segment.text().length() / 4); // 粗略估算 token 数
            chunk.setEmbedding(new PGvector(embedding.vector()));
            chunk.setEmbeddingModel(embeddingProperties.getModelName());
            chunk.setIsActive(true);
            chunk.setIsDeleted(false);
            chunk.setCrtim(new Date());
            chunk.setUptim(new Date());

            // 保存 metadata 为 JSON 字符串
            Map<String, Object> metadataMap = segment.metadata().toMap();
            try {
                chunk.setMetadata(objectMapper.writeValueAsString(metadataMap));
            } catch (Exception e) {
                log.warn("序列化 metadata 失败，使用空 JSON: {}", e.getMessage());
                chunk.setMetadata("{}");
            }

            chunkMapper.insert(chunk);
        }

        log.info("已保存 {} 个 chunks 到 ai_rag_chunk 表", segments.size());
    }

    /**
     * 停用旧的 chunks
     */
    private void deactivateChunks(Long documentId) {
        LambdaUpdateWrapper<AiRagChunk> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AiRagChunk::getDocumentId, documentId)
                .set(AiRagChunk::getIsActive, false)
                .set(AiRagChunk::getUptim, new Date());
        chunkMapper.update(null, updateWrapper);
    }

    /**
     * 更新文档处理状态
     */
    private void updateDocumentStatus(Long documentId, int status, String errorMessage) {
        LambdaUpdateWrapper<AiRagDocument> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AiRagDocument::getId, documentId)
                .set(AiRagDocument::getProcessStatus, status)
                .set(AiRagDocument::getProcessMessage, errorMessage)
                .set(AiRagDocument::getUptim, new Date());
        documentMapper.update(null, updateWrapper);
    }

    /**
     * 更新文档 chunk 数量
     */
    private void updateChunkCount(Long documentId, int chunkCount) {
        LambdaUpdateWrapper<AiRagDocument> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AiRagDocument::getId, documentId)
                .set(AiRagDocument::getChunkCount, chunkCount)
                .set(AiRagDocument::getUptim, new Date());
        documentMapper.update(null, updateWrapper);
    }
}
