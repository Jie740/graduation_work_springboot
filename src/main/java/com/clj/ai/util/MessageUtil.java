package com.clj.ai.util;

import com.clj.ai.dto.ChatRequestDto;
import com.clj.ai.service.MinioService;
import dev.langchain4j.data.message.Content;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息构建工具
 * <p>
 * 图片处理流程（Base64 仅在 Java → Qwen 这一段使用，不返回前端）：
 * 前端 imageUrls → Java → MinIO 读取 byte[] → Base64 data URL → Qwen
 *
 * @author ajie
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageUtil {

    private final MinioService minioService;

    public UserMessage buildUserMessage(ChatRequestDto chatRequestDto) {

        List<Content> contents = new ArrayList<>();

        // 文本
        if (chatRequestDto.getText() != null
                && !chatRequestDto.getText().isBlank()) {

            contents.add(
                    TextContent.from(chatRequestDto.getText())
            );
        }

        // 图片：从 MinIO 读取并转为 Base64 data URL 传给 Qwen
        if (chatRequestDto.getImageUrls() != null) {

            for (String imageUrl : chatRequestDto.getImageUrls()) {

                String dataUrl = minioService.readImageAsDataUrl(imageUrl);
                contents.add(
                        ImageContent.from(dataUrl)
                );
            }
        }

        return UserMessage.from(contents);
    }

}
