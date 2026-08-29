package com.clj.ai.config;

import com.clj.ai.service.AssistantService;
import com.clj.ai.tool.RagSearchTool;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI Service 配置
 * 通过 AiServices 动态代理构建 AssistantService，并注册 RAG 检索工具，
 * 由模型自主判断何时调用 listKnowledgeBases / searchKnowledge。
 */
@Configuration
@RequiredArgsConstructor
public class AiServiceConfig {

    private final RagSearchTool ragSearchTool;

    @Bean
    public AssistantService assistantService(
            ChatModel chatModel,
            StreamingChatModel streamingChatModel
    ) {
        return AiServices.builder(AssistantService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .tools(ragSearchTool)
                .build();
    }
}
