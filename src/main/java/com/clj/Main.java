package com.clj;

import java.util.Arrays;
import java.lang.System;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;

public class Main {

    public static GenerationResult callWithMessage() throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();

        // 【关键修改】这里定义了农业助手的角色、语气和服务范围
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("欢迎使用农业智能助手。\n" +
                        "我是您的农业专家，随时为您解答农业相关问题。我的专业领域包括：\n" +
                        "1. 农作物种植技术（播种、施肥、灌溉、收获等）\n" +
                        "2. 病虫害防治（识别症状、提供生物/化学防治方案）\n" +
                        "3. 土壤管理与改良\n" +
                        "4. 农业政策解读与市场分析\n" +
                        "5. 其他农业相关问题\n" +
                        "\n" +
                        "请保持回答专业、科学、实用，并尽量给出可操作的建议。如果用户的问题不明确，请引导他们提供更多细节（如作物种类、生长阶段、地理位置等）。")
                .build();

        // 这里的用户问题可以暂时保留为打招呼，或者你可以改成具体的农业问题来测试
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content("你好，请问你能帮我解决什么问题？")
                .build();

        GenerationParam param = GenerationParam.builder()
                // 请确保环境变量 DASHSCOPE_API_KEY 已设置，或者在这里直接填入 .apiKey("sk-xxx")
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))

                // 使用 qwen-plus 模型，它在农业等专业知识上表现良好
                .model("qwen-plus")
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();

        return gen.call(param);
    }

    public static void main(String[] args) {
        // 打印一个漂亮的欢迎头
        System.out.println("========================================");
        System.out.println("       🌾  欢迎使用农业智能助手  🌾      ");
        System.out.println("========================================");

        try {
            GenerationResult result = callWithMessage();
            String content = result.getOutput().getChoices().get(0).getMessage().getContent();

            System.out.println("\n🤖 农业专家回复：\n");
            System.out.println(content);

            System.out.println("\n----------------------------------------");
            System.out.println("如有具体问题，请修改代码中的 userMsg 内容再次运行。");

        } catch (NoApiKeyException e) {
            System.err.println("❌ 错误：未找到 API Key。");
            System.err.println("   请设置环境变量 'DASHSCOPE_API_KEY' 或在代码中直接填入 apiKey。");
        } catch (ApiException e) {
            System.err.println("❌ API 调用失败：" + e.getMessage());
            System.out.println("   请参考文档：https://help.aliyun.com/model-studio/developer-reference/error-code");
        } catch (InputRequiredException e) {
            System.err.println("❌ 输入参数错误: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ 发生未知错误: " + e.getMessage());
            e.printStackTrace();
        }

        System.exit(0);
    }
}