package com.clj.service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.clj.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DashScopeService {

    /**
     * 精简后的系统提示词（减少Token，提高速度）
     */
    private static final String SYSTEM_PROMPT =
            "你是农业专家，提供种植、病虫害、土壤等方面的专业建议，回答简洁且可操作。";

    /**
     * 复用Generation实例（避免重复创建）
     */
    private final Generation gen = new Generation();

    @Autowired
    private StringRedisTemplate redisTemplate;

    public Result callWithMessage(String userQuestion) {

        // ================== 1. 先查缓存 ==================
        String cacheKey = "ai:agriculture:" + userQuestion;
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);

        if (cacheValue != null) {
            log.info("命中缓存: {}", userQuestion);
            HashMap<String, String> map = new HashMap<>();
            map.put("message", cacheValue);
            return Result.ok(map);
        }

        // ================== 2. 构建消息 ==================
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content(SYSTEM_PROMPT)
                .build();

        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(userQuestion)
                .build();

        // ================== 3. 构建参数 ==================
        GenerationParam param = GenerationParam.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .model("qwen-turbo")   // ⭐ 使用更快模型
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .maxTokens(300)        // ⭐ 限制输出长度
                .build();

        try {
            long start = System.currentTimeMillis();

            // ================== 4. 调用模型 ==================
            GenerationResult result = gen.call(param);

            String content = result.getOutput()
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            long end = System.currentTimeMillis();
            log.info("AI响应耗时: {} ms", (end - start));

            // ================== 5. 写入缓存 ==================
            redisTemplate.opsForValue().set(cacheKey, content, 1, TimeUnit.HOURS);

            // ================== 6. 返回结果 ==================
            HashMap<String, String> map = new HashMap<>();
            map.put("message", content);

            return Result.ok(map);

        } catch (NoApiKeyException e) {
            log.error("API Key缺失: {}", e.getMessage());
            return Result.error("错误：未配置API密钥，请联系管理员");

        } catch (ApiException e) {
            log.error("API调用失败: {}", e.getMessage());
            return Result.error("错误：AI服务调用失败");

        } catch (InputRequiredException e) {
            log.error("输入参数错误: {}", e.getMessage());
            return Result.error("错误：输入参数异常");

        } catch (Exception e) {
            log.error("未知错误: {}", e.getMessage(), e);
            return Result.error("错误：系统内部错误");
        }
    }
}