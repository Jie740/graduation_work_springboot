package com.clj.ai.service;

import com.clj.ai.dto.RagSearchRequestDto;
import com.clj.ai.vo.RagSearchResultVo;

import java.util.List;

/**
 * RAG 向量检索服务
 */
public interface AiRagSearchService {

    /**
     * 向量检索
     *
     * @param request 检索请求
     * @return 检索结果列表
     */
    List<RagSearchResultVo> search(RagSearchRequestDto request);
}
