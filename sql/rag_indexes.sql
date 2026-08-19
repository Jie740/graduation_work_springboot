-- RAG 相关索引
-- 为 ai_rag_document 表添加索引
CREATE INDEX IF NOT EXISTS idx_rag_document_knowledge_base_id ON ai_rag_document(knowledge_base_id);
CREATE INDEX IF NOT EXISTS idx_rag_document_process_status ON ai_rag_document(process_status);
CREATE INDEX IF NOT EXISTS idx_rag_document_is_deleted ON ai_rag_document(is_deleted);

-- 为 ai_rag_chunk 表添加索引
CREATE INDEX IF NOT EXISTS idx_rag_chunk_document_id ON ai_rag_chunk(document_id);
CREATE INDEX IF NOT EXISTS idx_rag_chunk_is_active ON ai_rag_chunk(is_active);
CREATE INDEX IF NOT EXISTS idx_rag_chunk_is_deleted ON ai_rag_chunk(is_deleted);

-- 为 ai_rag_retrieval 表添加索引
CREATE INDEX IF NOT EXISTS idx_rag_retrieval_message_id ON ai_rag_retrieval(message_id);
CREATE INDEX IF NOT EXISTS idx_rag_retrieval_document_id ON ai_rag_retrieval(document_id);

-- 为 ai_rag_chunk 表的 embedding 字段创建 HNSW 索引（可选，根据数据量决定是否创建）
-- 注意：HNSW 索引会占用较多内存，建议数据量较大时再创建
-- CREATE INDEX IF NOT EXISTS idx_rag_chunk_embedding ON ai_rag_chunk USING hnsw (embedding vector_cosine_ops);
