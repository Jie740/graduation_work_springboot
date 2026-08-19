package com.clj.ai.service;

import com.clj.domain.User;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.spring.AiService;
import org.springframework.stereotype.Service;

import java.util.List;

public interface AssistantService {

    /**
     * 单轮聊天
     */
    @SystemMessage("你是一个农业助手，提供农业相关的信息和帮助。")
    ChatResponse chat(UserMessage userMessage);

    /**
     * 流式聊天
     */
    @SystemMessage("你是一个农业助手，提供农业相关的信息和帮助。")
    void chatStream(
            UserMessage userMessage,
            StreamingChatResponseHandler handler
    );
}
