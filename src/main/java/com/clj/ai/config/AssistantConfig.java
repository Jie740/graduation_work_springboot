package com.clj.ai.config;

import com.clj.ai.service.AssistantService;
import com.clj.ai.tool.RagSearchTool;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.*;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.time.Duration.ofSeconds;

@Configuration
@RequiredArgsConstructor
public class AssistantConfig {
    private final EmbeddingProperties properties;

    @Bean
    public ChatModel openAiChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(System.getenv("AL_API_KEY"))
                .modelName("qwen3-vl-flash")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .temperature(0.3)  //温度：介于0~2
                .maxTokens(1024)  //最大生成 tokens 数：介于1~4096
                .timeout(ofSeconds(20))
//                .logRequests(true)  //日志：请求日志
                .logResponses(true)  //日志：响应日志
                .build();
    }

    @Bean
    public StreamingChatModel openAiStreamingChatModel(){
        return OpenAiStreamingChatModel.builder()
                .apiKey(System.getenv("AL_API_KEY"))
                .modelName("qwen3-vl-flash")
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .temperature(0.3)  //温度：介于0~2
                .maxTokens(1024)  //最大生成 tokens 数：介于1~4096
                .timeout(ofSeconds(20))
//                .logRequests(true)  //日志：请求日志
                .logResponses(true)  //日志：响应日志
                .build();
    }

    /**
     * Embedding 模型配置
     * 使用 DashScope (阿里云) 的 Qwen Embedding 模型
     */

    @Bean
    public EmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(properties.getApiKey())
                .modelName(properties.getModelName())
                .baseUrl(properties.getBaseUrl())
                .dimensions(properties.getDimensions())
                .maxSegmentsPerBatch(10) // 阿里云的最大分段数：介于 1~10
                .timeout(ofSeconds(30))
                .build();
    }
}
