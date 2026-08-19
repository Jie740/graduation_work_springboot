package com.clj.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.clj.ai.domain.AiRagChunk;
import com.clj.ai.dto.RagSearchRequestDto;
import com.clj.ai.mapper.AiRagChunkMapper;
import com.clj.ai.service.AiRagSearchService;
import com.clj.ai.vo.RagSearchResultVo;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RAG 向量检索服务实现
 * 使用 LangChain4j EmbeddingStore 进行向量检索
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiRagSearchServiceImpl implements AiRagSearchService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final AiRagChunkMapper chunkMapper;

    @Override
    public List<RagSearchResultVo> search(RagSearchRequestDto request) {
        log.info("开始向量检索: knowledgeBaseId={}, query={}, topK={}, minScore={}",
                request.getKnowledgeBaseId(), request.getQuery(), request.getTopK(), request.getMinScore());

        // 1. 将查询文本向量化
        Embedding queryEmbedding = embeddingModel.embed(request.getQuery()).content();

        // 2. 使用 LangChain4j EmbeddingStore 进行检索
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(request.getTopK())
                .minScore(request.getMinScore())
                .build();

        EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);

        // 3. 转换结果
        List<RagSearchResultVo> results = new ArrayList<>();
        for (EmbeddingMatch<TextSegment> match : searchResult.matches()) {
            TextSegment segment = match.embedded();
            Map<String, Object> metadata = segment != null ? segment.metadata().toMap() : Map.of();

            // 直接从 metadata 获取 document_id 和 chunk_id（在保存时已写入）
            Long documentId = getLongFromMetadata(metadata, "document_id");
            Long chunkId = getLongFromMetadata(metadata, "chunk_id");

            RagSearchResultVo result = RagSearchResultVo.builder()
                    .documentId(documentId)
                    .chunkId(chunkId)
                    .content(segment != null ? segment.text() : "")
                    .score(match.score())
                    .metadata(metadata)
                    .build();

            results.add(result);
        }

        log.info("向量检索完成: 找到 {} 个结果", results.size());
        return results;
    }

    private Long getLongFromMetadata(Map<String, Object> metadata, String key) {
        Object value = metadata.get(key);
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Number) return ((Number) value).longValue();
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private Integer getIntegerFromMetadata(Map<String, Object> metadata, String key) {
        Object value = metadata.get(key);
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
