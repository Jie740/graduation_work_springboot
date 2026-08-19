package com.clj.ai.service.impl;

import com.clj.ai.service.AssistantService;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.service.TokenStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssistantServiceImpl implements AssistantService {

    private final ChatModel chatModel;
    private final StreamingChatModel streamingChatModel;


    @Override
    public ChatResponse chat(UserMessage userMessage) {
        return chatModel.chat(userMessage);
    }

    @Override
    public void chatStream(UserMessage userMessage, StreamingChatResponseHandler handler) {
        //TODO: 获取持久化记忆，转为ChatMessage并添加到messages中，以messages为参数调用streamingChatModel.chat
        streamingChatModel.chat(List.of(userMessage), handler);
    }
}