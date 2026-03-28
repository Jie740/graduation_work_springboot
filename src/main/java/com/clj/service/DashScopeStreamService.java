package com.clj.service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;

@Service
@Slf4j
public class DashScopeStreamService {

    private static final String SYSTEM_PROMPT =
            "你是农业专家，回答简洁、专业、可操作。";

    private final Generation gen = new Generation();

    public void streamChat(String question, HttpServletResponse response) {
        response.setContentType("text/event-stream;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter writer = response.getWriter()) {

            Message systemMsg = Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content(SYSTEM_PROMPT)
                    .build();

            Message userMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content(question)
                    .build();

            GenerationParam param = GenerationParam.builder()
                    .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                    .model("qwen-turbo")
                    .messages(Arrays.asList(systemMsg, userMsg))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .incrementalOutput(true)   // ⭐关键：开启流式
                    .build();

            // ⭐ 流式调用
            gen.streamCall(param)
                    .forEach(chunk -> {
                        try {
                            String content = chunk.getOutput()
                                    .getChoices()
                                    .get(0)
                                    .getMessage()
                                    .getContent();

                            if (content != null) {
                                // SSE格式
                                writer.write("data:" + content + "\n\n");
                                writer.flush();
                            }

                        } catch (Exception e) {
                            log.error("流式写入异常", e);
                        }
                    });

            // 结束标记
            writer.write("data:[DONE]\n\n");
            writer.flush();

        } catch (Exception e) {
            log.error("流式调用异常", e);
        }
    }
}