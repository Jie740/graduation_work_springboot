package com.clj.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.ai.domain.AiRagChunk;
import com.clj.ai.service.AiRagChunkService;
import com.clj.ai.mapper.AiRagChunkMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【ai_rag_chunk(AI知识库RAG文档分块及向量数据表)】的数据库操作Service实现
* @createDate 2026-08-18 18:06:04
*/
@Service
public class AiRagChunkServiceImpl extends ServiceImpl<AiRagChunkMapper, AiRagChunk>
    implements AiRagChunkService{

}




