-- 修复 ai_rag_chunk 表 content 字段类型
-- 将 varchar(1000) 改为 text，支持存储任意长度的文本内容

ALTER TABLE ai_rag_chunk 
ALTER COLUMN content TYPE text;

COMMENT ON COLUMN ai_rag_chunk.content IS 'Chunk文本内容，用于语义检索和大模型上下文增强（text类型，无长度限制）';
