package com.clj.ai.service.impl;

import com.clj.ai.dto.ChunkSimilarityDto;
import com.clj.ai.dto.RagSearchRequestDto;
import com.clj.ai.mapper.AiRagChunkMapper;
import com.clj.ai.service.AiRagSearchService;
import com.clj.ai.vo.RagSearchResultVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgvector.PGvector;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * RAG 向量检索服务实现
 * 基于 ai_rag_chunk 表的 pgvector 余弦相似度检索
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiRagSearchServiceImpl implements AiRagSearchService {

    private final EmbeddingModel embeddingModel;
    private final AiRagChunkMapper chunkMapper;
    private final ObjectMapper objectMapper;

    @Override
    public List<RagSearchResultVo> search(RagSearchRequestDto request) {
        // 合并单库与多库参数
        List<Long> knowledgeBaseIds = resolveKnowledgeBaseIds(request);

        log.info("开始向量检索: knowledgeBaseIds={}, query={}, topK={}, minScore={}",
                knowledgeBaseIds, request.getQuery(), request.getTopK(), request.getMinScore());

        // 1. 将查询文本向量化
        Embedding queryEmbedding = embeddingModel.embed(request.getQuery()).content();

        // 2. 基于 pgvector 余弦相似度检索 ai_rag_chunk
        List<ChunkSimilarityDto> similarChunks = chunkMapper.searchSimilar(
                new PGvector(queryEmbedding.vector()),
                request.getTopK(),
                knowledgeBaseIds);

        log.info("检索个数: {}", similarChunks.size());
        log.info("检索结果: {}", similarChunks.toString());
        // 3. 转换结果并按最小相似度过滤
        List<RagSearchResultVo> results = new ArrayList<>();
        for (ChunkSimilarityDto chunk : similarChunks) {
            if (chunk.getSimilarity() == null || chunk.getSimilarity() < request.getMinScore()) {
                continue;
            }

            RagSearchResultVo result = RagSearchResultVo.builder()
                    .documentId(chunk.getDocumentId())
                    .chunkId(chunk.getId())
                    .content(chunk.getContent())
                    .knowledgeBaseId(chunk.getKnowledgeBaseId())
                    .knowledgeBaseName(chunk.getKnowledgeBaseName())
                    .score(chunk.getSimilarity())
                    .metadata(parseMetadata(chunk.getMetadata()))
                    .build();

            results.add(result);
        }
        log.info("转换结果: {}", results.toString());

        log.info("向量检索完成: 找到 {} 个结果", results.size());
        return results;
    }

    /**
     * 合并 knowledgeBaseId（单库）与 knowledgeBaseIds（多库）参数
     *
     * @return 去重后的知识库ID列表；为空表示不限定知识库
     */
    private List<Long> resolveKnowledgeBaseIds(RagSearchRequestDto request) {
        List<Long> ids = new ArrayList<>();
        if (request.getKnowledgeBaseIds() != null) {
            ids.addAll(request.getKnowledgeBaseIds());
        }
        if (request.getKnowledgeBaseId() != null) {
            ids.add(request.getKnowledgeBaseId());
        }
        return ids.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    /**
     * 解析 metadata JSON 字符串为 Map
     */
    private Map<String, Object> parseMetadata(String metadata) {
        if (metadata == null || metadata.isBlank()) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(metadata, Map.class);
        } catch (Exception e) {
            log.warn("解析 metadata 失败: {}", e.getMessage());
            return Map.of();
        }
    }
}
