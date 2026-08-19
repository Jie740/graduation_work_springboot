package com.clj.ai.controller;

import com.clj.ai.dto.ChatRequestDto;
import com.clj.ai.service.AssistantService;
import com.clj.ai.util.MessageUtil;
import com.clj.common.exception.BusinessException;
import dev.langchain4j.data.message.Content;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ChatController {


    private final AssistantService assistantService;
    private final MessageUtil messageUtil;


    //    非流式输出
    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequestDto chatRequestDto) {
        UserMessage userMessage = messageUtil.buildUserMessage(chatRequestDto);
        return assistantService.chat(userMessage);
    }

    //    流式输出
    @GetMapping(
            value = "/chat-stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter chatStream(@RequestBody ChatRequestDto chatRequestDto) {
        // 0L不设置超时，生产建议改成30_000（30秒），防止连接泄露
        SseEmitter emitter = new SseEmitter(0L);

        assistantService.chatStream(
                messageUtil.buildUserMessage(chatRequestDto),
                new StreamingChatResponseHandler() {

                    @Override
                    public void onPartialResponse(String partialResponse) {
//                        log.info("onPartialResponse:{} " , partialResponse);
                        try {
                            // ✅ 关键：把每一段token推送给前端
                            emitter.send(SseEmitter.event().data(partialResponse));
                        } catch (Exception e) {
                            // 发送失败，关闭SSE连接
                            emitter.completeWithError(e);
                        }
                    }

                    @Override
                    public void onPartialThinking(PartialThinking partialThinking) {
                        log.info("onPartialThinking:{} " , partialThinking);
                        try {
                            // 如果要输出思考过程，也可以下发，前端区分类型
                            emitter.send(SseEmitter.event()
                                    .name("thinking")
                                    .data(partialThinking.text()));
                        } catch (Exception e) {
                            emitter.completeWithError(e);
                        }
                    }

                    @Override
                    public void onPartialToolCall(PartialToolCall partialToolCall) {
                        log.info("onPartialToolCall:{} " , partialToolCall);
                    }

                    @Override
                    public void onCompleteToolCall(CompleteToolCall completeToolCall) {
                        log.info("onCompleteToolCall:{} " , completeToolCall);
                    }

                    @Override
                    public void onCompleteResponse(ChatResponse completeResponse) {
                        log.info("onCompleteResponse:{} " , completeResponse);
                        // ✅ 流式全部结束，通知浏览器关闭连接
                        emitter.complete();
                    }

                    @Override
                    public void onError(Throwable error) {
                        // ✅ 异常的时候关闭SSE
                        emitter.completeWithError(error);
                        throw  new BusinessException(error.getMessage());
                    }
                }
        );
        return emitter;
    }
}
