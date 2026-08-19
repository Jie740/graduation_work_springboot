package com.clj.ai.controller;

import com.clj.ai.dto.RagSearchRequestDto;
import com.clj.ai.service.AiRagSearchService;
import com.clj.ai.vo.RagSearchResultVo;
import com.clj.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RAG 向量检索控制器
 */
@RestController
@RequestMapping("/ai/rag")
@RequiredArgsConstructor
public class AiRagSearchController {

    private final AiRagSearchService searchService;

    /**
     * 向量检索
     *
     * @param request 检索请求
     * @return 检索结果
     */
    @PostMapping("/search")
    public Result<List<RagSearchResultVo>> search(@RequestBody RagSearchRequestDto request) {
        List<RagSearchResultVo> results = searchService.search(request);
        return Result.success(results);
    }
}
