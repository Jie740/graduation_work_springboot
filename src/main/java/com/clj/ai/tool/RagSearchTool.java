package com.clj.ai.tool;

import com.clj.ai.domain.AiKnowledgeBase;
import com.clj.ai.dto.RagSearchRequestDto;
import com.clj.ai.service.AiKnowledgeBaseService;
import com.clj.ai.service.AiRagSearchService;
import com.clj.ai.vo.RagSearchResultVo;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * RAG 检索工具
 * <p>
 * 模型无法凭空知道 knowledgeBaseId，因此拆成两步：
 * 1. listKnowledgeBases() —— 让模型先"看见"有哪些知识库（id + 名称 + 描述）
 * 2. searchKnowledge()    —— 模型根据知识库描述挑选出相关的 1~N 个 id 发起检索
 * <p>
 * 这样既解决了"模型不知道 ID"的问题，也天然支持一次提问跨多个知识库检索。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RagSearchTool {

    private final AiRagSearchService searchService;
    private final AiKnowledgeBaseService knowledgeBaseService;

    /**
     * 列出全部可用知识库，供模型判断该去哪个（些）知识库检索
     */
    @Tool("列出所有可用的知识库，返回每个知识库的 id、名称和描述。"
            + "在调用 searchKnowledge 之前先调用本工具，以便根据知识库描述选择合适的 knowledgeBaseIds。")
    public String listKnowledgeBases() {
        List<AiKnowledgeBase> bases = knowledgeBaseService.lambdaQuery()
                .eq(AiKnowledgeBase::getIsDeleted, false)
                .eq(AiKnowledgeBase::getStatus, 1)
                .orderByAsc(AiKnowledgeBase::getId)
                .list();

        if (bases.isEmpty()) {
            return "当前没有可用的知识库。";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("可用知识库共 ").append(bases.size()).append(" 个：\n");
        for (AiKnowledgeBase base : bases) {
            sb.append("- id=").append(base.getId())
                    .append(" | 名称=").append(base.getName())
                    .append(" | 描述=")
                    .append(base.getDescription() == null || base.getDescription().isBlank()
                            ? "（无描述）" : base.getDescription())
                    .append(" | 文档数=").append(base.getDocumentCount())
                    .append("\n");
        }
        log.info("模型调用 listKnowledgeBases，返回 {} 个知识库", bases.size());
        return sb.toString();
    }

    /**
     * 向量检索，支持同时指定多个知识库
     *
     * @param query            检索语句
     * @param knowledgeBaseIds 要检索的知识库 id 列表；为空时检索全部启用的知识库
     * @param topK             每个知识库返回的最大条数
     */
    @Tool("在指定知识库中做向量检索，返回与问题最相关的资料片段。"
            + "当问题可能涉及专业知识（如作物种植、病虫害防治、施肥技术等）时调用；"
            + "可同时传入多个 knowledgeBaseIds 实现跨库检索。")
    public String searchKnowledge(
            @P("检索语句，使用用户问题的核心关键词或完整问句") String query,
            @P("要检索的知识库 id 列表，取自 listKnowledgeBases 返回的 id；留空表示检索全部知识库") List<Long> knowledgeBaseIds,
            @P("返回结果条数，默认 5") Integer topK
    ) {
        log.info("模型调用 searchKnowledge: query={}, knowledgeBaseIds={}, topK={}",
                query, knowledgeBaseIds, topK);

        RagSearchRequestDto request = new RagSearchRequestDto();
        request.setQuery(query);
        request.setKnowledgeBaseIds(knowledgeBaseIds);
//        request.setTopK(topK == null || topK <= 0 ? 5 : topK);
//        request.setMinScore(0.50);

        List<RagSearchResultVo> results = searchService.search(request);
        if (results.isEmpty()) {
            return "知识库中未检索到与该问题相关的内容。";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("检索到 ").append(results.size()).append(" 条相关资料：\n\n");
        for (int i = 0; i < results.size(); i++) {
            RagSearchResultVo r = results.get(i);
            sb.append("【资料 ").append(i + 1).append("】");
            if (r.getKnowledgeBaseName() != null) {
                sb.append("（知识库：").append(r.getKnowledgeBaseName()).append("）");
            }
            sb.append("\n").append(r.getContent()).append("\n");
            if (r.getScore() != null) {
                sb.append("相关度：").append(String.format("%.2f", r.getScore())).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
