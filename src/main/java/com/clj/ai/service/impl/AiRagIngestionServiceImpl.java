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
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
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
    private static final int MAX_SEGMENT_SIZE = 500;
    private static final int SEGMENT_OVERLAP_SIZE = 50;
    private static final int EMBEDDING_BATCH_SIZE = 10;

    /**
     * 章节标题匹配模式：支持 Markdown 标题、中文章节、数字章节和中文序号标题
     */
    private static final Pattern HEADING_PATTERN = Pattern.compile(
            "^\\s*(?:#{1,6}\\s+.+|第[一二三四五六七八九十百千万0-9]+[章节篇部分].*|"
                    + "[0-9]{1,3}(?:\\.[0-9]{1,3}){0,4}[、.．)）\\s].*|"
                    + "[一二三四五六七八九十]+[、.．)）\\s].*)$",
            Pattern.MULTILINE);

    private record HeadingPosition(int offset, String title) {
    }

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
            if (!tryStartProcessing(documentId)) {
                log.warn("文档正在处理中，跳过重复任务: documentId={}", documentId);
                return;
            }

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
            deactivateChunks(documentId);
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
            if (!tryStartProcessing(documentId)) {
                log.warn("文档正在处理中，跳过重复索引任务: documentId={}", documentId);
                return;
            }

            // 1. 停用旧的 chunks
            deactivateChunks(documentId);

            // 2. 重新处理文档
            Document langChainDocument = loadDocument(document);
            List<TextSegment> segments = splitDocument(langChainDocument, document);
            List<Embedding> embeddings = generateEmbeddings(segments);
            saveChunks(document, segments, embeddings);

            // 3. 更新状态
            deleteInactiveChunks(documentId);
            updateDocumentStatus(documentId, STATUS_SUCCESS, null);
            updateChunkCount(documentId, segments.size());

            log.info("文档重新索引完成: documentId={}, chunks={}", documentId, segments.size());

        } catch (Exception e) {
            log.error("文档重新索引失败: documentId={}", documentId, e);
            deactivateChunks(documentId);
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

        // 统一换行符，保留 \f 作为 PDF 页边界
        text = text.replace("\r\n", "\n").replace('\r', '\n');

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

            if ("pdf".equalsIgnoreCase(document.getFileType())) {
                return Document.from(cleanText(extractPdfText(inputStream)));
            }

            ApacheTikaDocumentParser parser = new ApacheTikaDocumentParser();
            Document doc = parser.parse(inputStream);
            return Document.from(cleanText(doc.text()), doc.metadata());

        } catch (Exception e) {
            throw new RuntimeException(
                    "文档解析失败: " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * 使用 PDFBox 保留 PDF 页边界，避免仅依赖 Tika 输出导致所有 chunk 都被标记为第 1 页。
     */
    private String extractPdfText(InputStream inputStream) throws Exception {
        try (PDDocument pdfDocument = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            stripper.setPageEnd("\f");
            return stripper.getText(pdfDocument);
        }
    }

    /**
     * 使用 LangChain4j 切块
     */
    private List<TextSegment> splitDocument(Document document, AiRagDocument ragDocument) {
        // 500 字符分块，保留 50 字符重叠，保证跨分块语义不丢失
        var splitter = DocumentSplitters.recursive(MAX_SEGMENT_SIZE, SEGMENT_OVERLAP_SIZE);

        List<TextSegment> segments = removeDuplicateSegments(splitter.split(document));
        if (segments.isEmpty()) {
            throw new IllegalArgumentException("文档解析后没有可用文本");
        }

        // 全文用于定位每个 segment 的偏移，从而推算页码与所属章节
        String fullText = document.text();
        String documentTitle = extractTitle(document, ragDocument);
        List<Integer> pageBreakOffsets = collectPageBreakOffsets(fullText);
        List<HeadingPosition> headingPositions = collectHeadingPositions(fullText);

        int previousOffset = -1;
        int previousLength = 0;
        int pageBreakIndex = 0;
        int headingIndex = 0;
        String currentSection = "";

        // 为每个 segment 添加 metadata（页码、章节、标题、来源文件等）
        for (int i = 0; i < segments.size(); i++) {
            TextSegment segment = segments.get(i);
            Map<String, Object> metadata = new HashMap<>(segment.metadata().toMap());

            // 允许从上一块内部开始查找，避免 overlap 导致定位失败。
            int offset = locateSegment(fullText, segment.text(), previousOffset, previousLength);
            if (offset < previousOffset) {
                log.warn("分块定位出现回退: documentId={}, chunkIndex={}, offset={}, previousOffset={}",
                        ragDocument.getId(), i, offset, previousOffset);
            }

            while (pageBreakIndex < pageBreakOffsets.size()
                    && pageBreakOffsets.get(pageBreakIndex) < offset) {
                pageBreakIndex++;
            }
            int pageNumber = pageBreakIndex + 1;

            while (headingIndex < headingPositions.size()
                    && headingPositions.get(headingIndex).offset() <= offset) {
                currentSection = headingPositions.get(headingIndex).title();
                headingIndex++;
            }

            metadata.put("document_id", ragDocument.getId());
            metadata.put("knowledge_base_id", ragDocument.getKnowledgeBaseId());
            metadata.put("file_name", ragDocument.getFileName());
            metadata.put("source", ragDocument.getObjectName());
            metadata.put("chunk_index", i);
            metadata.put("page_number", pageNumber);
            metadata.put("pageNum", pageNumber);
            metadata.put("section", currentSection);
            metadata.put("title", documentTitle);

            segments.set(i, TextSegment.from(segment.text(), dev.langchain4j.data.document.Metadata.from(metadata)));
            previousOffset = offset;
            previousLength = segment.text().length();
        }

        return segments;
    }

    /**
     * 去除切块器异常产生的完全重复块。保留首个块，避免同一文本重复调用 embedding 并重复入库。
     */
    private List<TextSegment> removeDuplicateSegments(List<TextSegment> segments) {
        Set<String> seen = new HashSet<>();
        List<TextSegment> uniqueSegments = new ArrayList<>(segments.size());
        int duplicateCount = 0;

        for (TextSegment segment : segments) {
            String fingerprint = normalizeForFingerprint(segment.text());
            if (fingerprint.isBlank() || !seen.add(fingerprint)) {
                duplicateCount++;
                continue;
            }
            uniqueSegments.add(segment);
        }

        if (duplicateCount > 0) {
            log.warn("已移除重复文档分块: duplicateChunks={}", duplicateCount);
        }
        return uniqueSegments;
    }

    /**
     * 仅用于判断两个 chunk 是否为同一文本，忽略空白差异。
     */
    private String normalizeForFingerprint(String text) {
        return text == null ? "" : text.replaceAll("\\s+", " ").trim();
    }

    private List<Integer> collectPageBreakOffsets(String fullText) {
        List<Integer> offsets = new ArrayList<>();
        for (int i = 0; i < fullText.length(); i++) {
            if (fullText.charAt(i) == '\f') {
                offsets.add(i);
            }
        }
        return offsets;
    }

    private List<HeadingPosition> collectHeadingPositions(String fullText) {
        List<HeadingPosition> positions = new ArrayList<>();
        Matcher matcher = HEADING_PATTERN.matcher(fullText);
        while (matcher.find()) {
            positions.add(new HeadingPosition(matcher.start(), matcher.group().trim()));
        }
        return positions;
    }

    /**
     * 根据相邻分块的预期位置定位当前分块，支持 50 字符 overlap。
     */
    private int locateSegment(String fullText,
                              String segmentText,
                              int previousOffset,
                              int previousLength) {
        if (previousOffset < 0) {
            int firstOffset = fullText.indexOf(segmentText);
            return firstOffset >= 0 ? firstOffset : 0;
        }

        int minimumStart = Math.min(fullText.length(), previousOffset + 1);
        int expectedStart = Math.max(
                minimumStart,
                previousOffset + Math.max(1, previousLength - SEGMENT_OVERLAP_SIZE)
        );

        int offset = fullText.indexOf(segmentText, expectedStart);
        if (offset >= 0) {
            return offset;
        }

        offset = fullText.indexOf(segmentText, minimumStart);
        return offset >= 0 ? offset : expectedStart;
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
        List<Embedding> embeddings = new ArrayList<>(segments.size());

        for (int from = 0; from < segments.size(); from += EMBEDDING_BATCH_SIZE) {
            int to = Math.min(from + EMBEDDING_BATCH_SIZE, segments.size());
            List<Embedding> batchEmbeddings =
                    embeddingModel.embedAll(segments.subList(from, to)).content();

            if (batchEmbeddings == null || batchEmbeddings.size() != to - from) {
                throw new IllegalStateException(String.format(
                        "向量数量与分块数量不一致: chunks=%d, embeddings=%d",
                        to - from,
                        batchEmbeddings == null ? 0 : batchEmbeddings.size()));
            }

            embeddings.addAll(batchEmbeddings);
            log.info("文档向量化进度: {}/{}", to, segments.size());
        }

        return embeddings;
    }

    /**
     * 保存 chunks 到数据库（文本与向量统一存储在 ai_rag_chunk 表）
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveChunks(AiRagDocument document, List<TextSegment> segments, List<Embedding> embeddings) {
        if (segments.size() != embeddings.size()) {
            throw new IllegalArgumentException("分块数量与向量数量不一致，取消入库");
        }

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

    private void deleteInactiveChunks(Long documentId) {
        chunkMapper.deleteInactiveByDocumentId(documentId);
    }

    /**
     * 原子抢占文档处理任务，避免同一文档被多个异步任务同时切块和入库。
     */
    private boolean tryStartProcessing(Long documentId) {
        LambdaUpdateWrapper<AiRagDocument> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AiRagDocument::getId, documentId)
                .ne(AiRagDocument::getProcessStatus, STATUS_PROCESSING)
                .set(AiRagDocument::getProcessStatus, STATUS_PROCESSING)
                .set(AiRagDocument::getProcessMessage, null)
                .set(AiRagDocument::getUptim, new Date());
        return documentMapper.update(null, updateWrapper) > 0;
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
