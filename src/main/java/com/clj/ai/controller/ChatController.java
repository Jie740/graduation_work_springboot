package com.clj.ai.controller;

import com.clj.ai.dto.ChatRequestDto;
import com.clj.ai.service.AssistantService;
import com.clj.ai.util.MessageUtil;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.service.TokenStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ChatController {

    private final AssistantService assistantService;
    private final MessageUtil messageUtil;

    /**
     * 流式输出接口
     * 支持模型自主判断是否调用 RAG 检索工具
     */
    @GetMapping(
            value = "/chat-stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter chatStream(@RequestBody ChatRequestDto chatRequestDto) {
        // 0L不设置超时，生产建议改成30_000（30秒），防止连接泄露
        SseEmitter emitter = new SseEmitter(0L);

        try {
            UserMessage userMessage = messageUtil.buildUserMessage(chatRequestDto);

            // 使用 TokenStream 支持工具调用
            TokenStream tokenStream = assistantService.chatStream(userMessage);

            // 订阅 TokenStream 的事件
            tokenStream
                    .onPartialResponse(token -> {
                        try {
                            // 发送普通文本响应
                            if (token != null && !token.isEmpty()) {
                                emitter.send(SseEmitter.event().data(token));
                            }
                        } catch (Exception e) {
                            log.error("发送部分响应失败", e);
                            emitter.completeWithError(e);
                        }
                    })
                    .beforeToolExecution(beforeToolExecution -> {
                        // 工具即将执行：通知前端正在检索知识库
                        try {
                            log.info("模型即将调用工具: {}", beforeToolExecution.request().name());
                            emitter.send(SseEmitter.event()
                                    .name("tool_call")
                                    .data("正在检索知识库..."));
                        } catch (Exception e) {
                            log.error("发送工具调用事件失败", e);
                        }
                    })
                    .onToolExecuted(toolExecution -> {
                        // 工具执行完成
                        log.info("模型工具调用完成: {}", toolExecution.request().name());
                    })
                    .onCompleteResponse(response -> {
                        try {
                            log.info("流式响应完成");
                            emitter.send(SseEmitter.event().name("done").data(""));
                            emitter.complete();
                        } catch (Exception e) {
                            log.error("完成响应时出错", e);
                            emitter.completeWithError(e);
                        }
                    })
                    .onError(error -> {
                        log.error("流式响应出错", error);
                        try {
                            emitter.send(SseEmitter.event()
                                    .name("error")
                                    .data("处理出错: " + error.getMessage()));
                        } catch (Exception e) {
                            log.error("发送错误事件失败", e);
                        }
                        emitter.completeWithError(error);
                    })
                    .start();

        } catch (Exception e) {
            log.error("启动流式响应失败", e);
            emitter.completeWithError(e);
        }

        return emitter;
    }
}
