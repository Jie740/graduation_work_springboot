-- =====================================================
-- RAG 向量存储表（LangChain4j PgVectorEmbeddingStore 专用）
-- =====================================================
-- 兼容说明：
-- LangChain4j PgVectorEmbeddingStore (1.3.0-beta9) 默认表结构为：
--   embedding_id (uuid), embedding (vector), text (text), metadata (jsonb)
-- 注意：文本列名必须是 text，不能是 content，否则插入时报
-- "column \"text\" of relation does not exist"
-- 现有 ai_rag_chunk 表包含大量业务字段（document_id, chunk_index, is_active, is_deleted 等），
-- 与 PgVectorEmbeddingStore 的表结构不兼容。
-- 采用最小改动方案：创建独立的 ai_rag_embedding 表供 PgVectorEmbeddingStore 使用，
-- 通过 metadata 中的 chunk_id 关联 ai_rag_chunk 业务表。
-- =====================================================

CREATE TABLE IF NOT EXISTS ai_rag_embedding (
    embedding_id UUID PRIMARY KEY,
    embedding vector(1024),
    text text,
    metadata jsonb,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE ai_rag_embedding IS 'LangChain4j PgVectorEmbeddingStore 向量存储表';
COMMENT ON COLUMN ai_rag_embedding.embedding_id IS '向量唯一ID (UUID)';
COMMENT ON COLUMN ai_rag_embedding.embedding IS '文本 Embedding 向量';
COMMENT ON COLUMN ai_rag_embedding.text IS 'Chunk 文本内容';
COMMENT ON COLUMN ai_rag_embedding.metadata IS '元数据 JSON，包含 chunk_id, document_id, knowledge_base_id, file_name, chunk_index';
COMMENT ON COLUMN ai_rag_embedding.created_at IS '创建时间';

-- 为 metadata 中的 chunk_id 创建 GIN 索引，加速关联查询
CREATE INDEX IF NOT EXISTS idx_rag_embedding_metadata ON ai_rag_embedding USING gin (metadata);
