package com.clj.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatRequestDto {
    /**
     * 模型名称
     */
    private String model;
    /**
     * 会话ID
     */
    private Long conversationId;

    /**
     * 用户输入文本
     */
    private String text;

    /**
     * 图片URL（可选）
     */
    private List<String> imageUrls;

    /**
     * 其他参数（可选）
     */
}
