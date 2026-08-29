package com.clj.ai.mapper;

import com.clj.ai.domain.AiRagChunk;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.clj.ai.dto.ChunkSimilarityDto;
import com.pgvector.PGvector;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ajie
 * @description 针对表【ai_rag_chunk(AI知识库RAG文档分块及向量数据表)】的数据库操作Mapper
 * @createDate 2026-08-18 18:06:04
 * @Entity com.clj.ai.domain.AiRagChunk
 */
public interface AiRagChunkMapper extends BaseMapper<AiRagChunk> {

    /**
     * 向量相似度检索（余弦相似度），仅检索启用且未删除的分块
     *
     * @param queryVector      查询向量
     * @param topK             返回数量
     * @param knowledgeBaseIds 知识库ID列表（可选，为空时不限制）
     */
    List<ChunkSimilarityDto> searchSimilar(@Param("queryVector") PGvector queryVector,
                                           @Param("topK") int topK,
                                           @Param("knowledgeBaseIds") List<Long> knowledgeBaseIds);

    /**
     * 查询指定文档的所有分块
     */
    List<AiRagChunk> selectByDocumentId(@Param("documentId") Long documentId);

    /**
     * 物理删除指定文档的所有分块
     */
    int deleteByDocumentId(@Param("documentId") Long documentId);

    /**
     * 删除指定文档重建索引后遗留的失效分块
     */
    int deleteInactiveByDocumentId(@Param("documentId") Long documentId);
}
