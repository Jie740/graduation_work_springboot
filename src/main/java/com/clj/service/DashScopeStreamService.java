package com.clj.service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.clj.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DashScopeStreamService {

    /**
     * 精简后的系统提示词
     */
    private static final String SYSTEM_PROMPT =
            "你是农业专家，提供种植、病虫害、土壤等方面的专业建议，回答简洁且可操作。";

    /**
     * 复用Generation实例（避免重复创建）
     */
    private final Generation gen = new Generation();

    @Autowired
    private StringRedisTemplate redisTemplate;

    // ✅ 上下文缓存（生产环境使用 Redis）
    private static final Map<String, List<Message>> CONTEXT_MAP = new ConcurrentHashMap<>();

    // ===== 农业知识库（可换数据库/向量库）=====
    private String buildKnowledge(String question) {

        if (question.contains("水稻")) {
            return "水稻种植要点：\n1. 保持水层3-5cm\n2. 控制氮肥\n3. 防治稻飞虱";
        }

        if (question.contains("西红柿")) {
            return "西红柿种植要点：\n1. 温度20-28℃\n2. 定期打杈\n3. 控湿防病";
        }

        if (question.contains("小麦")) {
            return "小麦管理要点：\n1. 适时播种\n2. 控制密度\n3. 防治锈病";
        }

        return "";
    }

    /**
     * 流式对话接口
     *
     * @param question 用户问题
     * @param userId   用户ID（用于维护对话上下文）
     * @return SseEmitter 用于流式输出
     */
    public SseEmitter streamChat(String question, String userId) {

        SseEmitter emitter = new SseEmitter(0L);

        new Thread(() -> {
            try {
                long start = System.currentTimeMillis();

                // ===== 1. 获取历史对话 =====
                List<Message> history = CONTEXT_MAP.getOrDefault(userId, new ArrayList<>());

                // ===== 2. 知识库增强 =====
                String knowledge = buildKnowledge(question);

                String finalPrompt = knowledge.isEmpty()
                        ? question
                        : "【农业知识参考】\n" + knowledge + "\n\n【用户问题】" + question;

                // ===== 3. 构造消息 =====
                List<Message> messages = new ArrayList<>();

                messages.add(Message.builder()
                        .role("system")
                        .content(SYSTEM_PROMPT)
                        .build());

                messages.addAll(history);

                messages.add(Message.builder()
                        .role("user")
                        .content(finalPrompt)
                        .build());

                GenerationParam param = GenerationParam.builder()
                        .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                        .model("qwen-turbo")   // ⭐ 使用更快模型
                        .messages(messages)
                        .incrementalOutput(true)
                        .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                        .maxTokens(300)        // ⭐ 限制输出长度
                        .build();

                StringBuilder answer = new StringBuilder();

                // ===== 4. 流式输出 =====
                gen.streamCall(param).subscribe(

                        chunk -> {
                            try {
                                String content = chunk.getOutput()
                                        .getChoices()
                                        .get(0)
                                        .getMessage()
                                        .getContent();

                                if (content != null) {
                                    answer.append(content);
                                    emitter.send(
                                            SseEmitter.event()
                                                    .data(content)
                                    );
                                }

                            } catch (Exception e) {
                                log.error("流式输出失败: {}", e.getMessage());
                                emitter.completeWithError(e);
                            }
                        },

                        error -> {
                            log.error("流式调用错误: {}", error.getMessage());
                            emitter.completeWithError(error);
                        },

                        () -> {
                            try {
                                long end = System.currentTimeMillis();
                                log.info("AI流式响应耗时: {} ms", (end - start));

                                // ===== 5. 保存上下文到 Redis =====
                                history.add(Message.builder()
                                        .role("user")
                                        .content(question)
                                        .build());

                                history.add(Message.builder()
                                        .role("assistant")
                                        .content(answer.toString())
                                        .build());

                                // 限制历史记录长度，避免超出 Token 限制
                                List<Message> newHistory = history;
                                if (newHistory.size() > 10) {
                                    newHistory = new ArrayList<>(
                                            newHistory.subList(newHistory.size() - 10, newHistory.size())
                                    );
                                }

                                // ✅ 保存到 Redis（1小时过期）
                                String contextKey = "ai:context:" + userId;
                                redisTemplate.opsForValue().set(contextKey, newHistory.toString(), 1, TimeUnit.HOURS);
                                CONTEXT_MAP.put(userId, newHistory);

                                emitter.complete();

                            } catch (Exception e) {
                                log.error("保存上下文失败: {}", e.getMessage());
                                emitter.completeWithError(e);
                            }
                        }
                );

            } catch (NoApiKeyException e) {
                log.error("API Key缺失: {}", e.getMessage());
                try {
                    emitter.send(SseEmitter.event().data("错误：未配置API密钥，请联系管理员"));
                    emitter.complete();
                } catch (Exception ex) {
                    emitter.completeWithError(ex);
                }

            } catch (ApiException e) {
                log.error("API调用失败: {}", e.getMessage());
                try {
                    emitter.send(SseEmitter.event().data("错误：AI服务调用失败"));
                    emitter.complete();
                } catch (Exception ex) {
                    emitter.completeWithError(ex);
                }

            } catch (InputRequiredException e) {
                log.error("输入参数错误: {}", e.getMessage());
                try {
                    emitter.send(SseEmitter.event().data("错误：输入参数异常"));
                    emitter.complete();
                } catch (Exception ex) {
                    emitter.completeWithError(ex);
                }

            } catch (Exception e) {
                log.error("未知错误: {}", e.getMessage(), e);
                try {
                    emitter.send(SseEmitter.event().data("错误：系统内部错误"));
                    emitter.complete();
                } catch (Exception ex) {
                    emitter.completeWithError(ex);
                }
            }
        }).start();

        return emitter;
    }

}