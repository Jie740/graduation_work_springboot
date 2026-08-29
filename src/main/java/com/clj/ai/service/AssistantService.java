package com.clj.ai.service;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;

public interface AssistantService {

    String AGRI_RAG_PROMPT = """
            你是一个农业助手，提供农业相关的信息和帮助。
            你可以使用以下工具检索知识库：
            1. listKnowledgeBases：列出所有可用知识库（id、名称、描述）。
            2. searchKnowledge：在指定知识库中做向量检索。
            使用规则：
            - 当用户问题可能涉及农业专业知识（如作物种植、病虫害、施肥等）时，
              先调用 listKnowledgeBases 了解有哪些知识库，再根据知识库描述
              选择一个或多个相关的 knowledgeBaseIds 调用 searchKnowledge；
              问题涉及多个领域时应同时传入多个知识库 id。
            - 闲聊或常识问题无需检索，直接回答。
            - 回答时以检索到的资料为依据，不要编造知识库中没有的内容。
            """;

    /**
     * 单轮聊天
     */
    @SystemMessage(AGRI_RAG_PROMPT)
    ChatResponse chat(UserMessage userMessage);

    /**
     * 流式聊天 - 返回 TokenStream 以支持工具调用
     */
    @SystemMessage(AGRI_RAG_PROMPT)
    TokenStream chatStream(UserMessage userMessage);
}
