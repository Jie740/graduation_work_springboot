package com.clj.service;

import cn.hutool.json.JSONUtil;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.clj.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DashScopeService {

    /**
     * ⭐ 强化系统提示词（避免模型“失忆”）
     */
    private static final String SYSTEM_PROMPT =
            "你是农业专家，请基于完整对话上下文回答问题，不能忽略历史信息。" +
                    "如果用户问题依赖上下文，请结合之前内容回答。";

    /**
     * ⭐ 上下文轮数（建议 10~20）
     */
    private static final int MAX_HISTORY = 15;

    private final Generation gen = new Generation();

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CHAT_PREFIX = "ai:chat:";
    private static final String COUNT_PREFIX = "ai:count:";
    private static final String FAQ_PREFIX = "ai:faq:";

    /**
     * 常见问题列表（用于缓存）- 已标准化的问题
     */
    private static final Set<String> FAQ_QUESTIONS = new HashSet<>(Arrays.asList(
            "水稻叶片发黄怎么办",      // 去除标点后的标准化形式
            "番茄常见病虫害有哪些",
            "小麦什么时候播种最好",
            "土壤酸化怎么改良",
            "玉米施肥的最佳时间",
            "果树修剪有什么技巧"
    ));

    public Result callWithContext(String userId, String userQuestion) {

        String redisKey = CHAT_PREFIX + userId;
        String countKey = COUNT_PREFIX + userId;

        // ================== ⭐ 0. 特殊问题拦截（避免AI乱算） ==================
        if (userQuestion.contains("几次")) {
            String count = redisTemplate.opsForValue().get(countKey);
            count = (count == null) ? "0" : count;

            HashMap<String, String> map = new HashMap<>();
            map.put("message", "你一共问了 " + count + " 次");
            return Result.ok(map);
        }

        // ================== ⭐ 0.5. 常见问题缓存检查 ==================
        String normalizedQuestion = normalizeQuestion(userQuestion);
        if (FAQ_QUESTIONS.contains(normalizedQuestion)) {
            String faqKey = FAQ_PREFIX + normalizedQuestion;
            String cachedAnswer = redisTemplate.opsForValue().get(faqKey);
            
            if (cachedAnswer != null) {
                log.info("命中常见问题缓存: {}", normalizedQuestion);
                
                // ================== ⭐ 保存命中缓存的问答到用户上下文 ==================
                saveMessage(redisKey, Message.builder()
                        .role(Role.USER.getValue())
                        .content(userQuestion)
                        .build());
                
                saveMessage(redisKey, Message.builder()
                        .role(Role.ASSISTANT.getValue())
                        .content(cachedAnswer)
                        .build());
                
                // 控制长度
                trimHistory(redisKey);
                
                HashMap<String, String> map = new HashMap<>();
                map.put("message", cachedAnswer);
                map.put("fromCache", "true");
                return Result.ok(map);
            }
            
            log.info("未命中常见问题缓存，将调用AI并缓存: {}", normalizedQuestion);
        }

        // ================== ⭐ 1. 统计问题次数 ==================
        redisTemplate.opsForValue().increment(countKey);
        redisTemplate.expire(countKey, 1, TimeUnit.HOURS);

        // ================== 2. 获取历史记录 ==================
        List<String> historyJsonList = redisTemplate.opsForList()
                .range(redisKey, 0, -1);

        List<Message> messages = new ArrayList<>();

        // system prompt
        messages.add(Message.builder()
                .role(Role.SYSTEM.getValue())
                .content(SYSTEM_PROMPT)
                .build());

        // ================== ⭐ 3. 历史记录（只取最近N轮） ==================
        if (historyJsonList != null && !historyJsonList.isEmpty()) {

            int start = Math.max(historyJsonList.size() - MAX_HISTORY * 2, 0);

            for (int i = start; i < historyJsonList.size(); i++) {
                Message msg = JSONUtil.toBean(historyJsonList.get(i), Message.class);
                messages.add(msg);
            }
        }

        // ================== ⭐ 4. 增强用户问题（防止上下文断裂） ==================
        String enhancedQuestion = "基于之前对话：" + userQuestion;

        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(enhancedQuestion)
                .build();

        messages.add(userMsg);

        // ================== ⭐ 5. 打印上下文（调试必备） ==================
        log.info("当前上下文: {}", JSONUtil.toJsonStr(messages));

        // ================== 6. 构建参数 ==================
        GenerationParam param = GenerationParam.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .model("qwen-turbo")
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .maxTokens(1000) // ⭐ 提高一点，避免截断
                .build();

        try {
            long startTime = System.currentTimeMillis();

            // ================== 7. 调用模型 ==================
            GenerationResult result = gen.call(param);

            String content = result.getOutput()
                    .getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            long endTime = System.currentTimeMillis();
            log.info("AI响应耗时: {} ms", (endTime - startTime));
            
            // ================== ⭐ 7.5. 打印AI回复内容 ==================
            log.info("========== AI回复内容 ==========");
            log.info("{}", content);
            log.info("================================");

            // ================== 8. 保存上下文 ==================
            saveMessage(redisKey, Message.builder()
                    .role(Role.USER.getValue())
                    .content(userQuestion) // ⭐ 注意：存原始问题
                    .build());

            saveMessage(redisKey, Message.builder()
                    .role(Role.ASSISTANT.getValue())
                    .content(content)
                    .build());

            // ================== ⭐ 8.5. 如果是常见问题，缓存回答 ==================
            if (FAQ_QUESTIONS.contains(normalizedQuestion)) {
                String faqKey = FAQ_PREFIX + normalizedQuestion;
                // 缓存7天
                redisTemplate.opsForValue().set(faqKey, content, 7, TimeUnit.DAYS);
                log.info("已缓存常见问题回答: {}", normalizedQuestion);
            }

            // ================== ⭐ 9. 控制长度（更安全） ==================
            trimHistory(redisKey);

            // ================== 10. 返回 ==================
            HashMap<String, String> map = new HashMap<>();
            map.put("message", content);

            return Result.ok(map);

        } catch (Exception e) {
            log.error("AI调用异常", e);
            return Result.error("AI服务异常");
        }
    }

    /**
     * 保存消息
     */
    private void saveMessage(String key, Message message) {
        redisTemplate.opsForList().rightPush(key, JSONUtil.toJsonStr(message));
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
    }

    /**
     * ⭐ 更安全的裁剪方式
     */
    private void trimHistory(String key) {
        Long size = redisTemplate.opsForList().size(key);

        if (size != null && size > MAX_HISTORY * 2) {
            redisTemplate.opsForList().trim(key, size - MAX_HISTORY * 2, -1);
        }
    }

    /**
     * 标准化问题（去除空格、标点等，提高匹配率）
     */
    private String normalizeQuestion(String question) {
        if (question == null) {
            return "";
        }
        // 去除首尾空格，统一标点
        return question.trim()
                .replaceAll("\\s+", "")  // 去除所有空格
                .replaceAll("[\uff0c\uff0e\uff1f\uff01\u3001]", ""); // 去除中文标点
    }

    /**
     * 获取用户对话上下文
     */
    public Result getChatHistory(String userId) {
        String redisKey = CHAT_PREFIX + userId;
        List<String> historyJsonList = redisTemplate.opsForList().range(redisKey, 0, -1);
        
        if (historyJsonList == null || historyJsonList.isEmpty()) {
            return Result.ok(new ArrayList<>());
        }
        
        // 转换为Message对象列表
        List<Message> messages = new ArrayList<>();
        for (String json : historyJsonList) {
            try {
                Message msg = JSONUtil.toBean(json, Message.class);
                messages.add(msg);
            } catch (Exception e) {
                log.error("解析消息失败: {}", json, e);
            }
        }
        System.out.println(messages);
        return Result.ok(messages);
    }

    /**
     * 清空用户对话上下文
     */
    public Result clearChatHistory(String userId) {
        String redisKey = CHAT_PREFIX + userId;
        String countKey = COUNT_PREFIX + userId;
        
        // 删除对话历史记录
        redisTemplate.delete(redisKey);
        // 删除计数记录
        redisTemplate.delete(countKey);
        
        log.info("已清空用户 {} 的对话历史", userId);
        return Result.ok("对话历史已清空");
    }
}