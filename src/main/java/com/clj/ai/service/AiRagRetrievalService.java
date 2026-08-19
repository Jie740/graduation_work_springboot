package com.clj.ai.service;

import com.clj.ai.domain.AiRagRetrieval;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * RAG 检索溯源服务
 */
public interface AiRagRetrievalService extends IService<AiRagRetrieval> {

    /**
     * 批量保存检索溯源记录
     *
     * @param messageId 消息ID
     * @param retrievals 检索结果列表
     */
    void saveRetrievals(Long messageId, List<RetrievalInfo> retrievals);

    /**
     * 检索结果信息
     */
    record RetrievalInfo(Long documentId, Long chunkId, BigDecimal score) {
    }
}
