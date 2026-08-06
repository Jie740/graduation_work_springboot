package com.clj.controller;

import com.clj.domain.User;
import com.clj.service.DashScopeService;
import com.clj.service.DashScopeStreamService;
import com.clj.utils.JwtUtils;
import com.clj.utils.Result;
import com.clj.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("assistant")
@RequiredArgsConstructor
public class ChatController {

    private final DashScopeService dashScopeService;
    private final DashScopeStreamService streamService;

    @GetMapping(value = "/stream", produces = "text/event-stream;charset=UTF-8")

    public SseEmitter stream(String question, HttpServletRequest request, HttpServletResponse response) {
        // 从请求头获取token并解析userId
        Long userId = UserHolder.getUserId();
        
        response.setCharacterEncoding("UTF-8");   // ✅ 核心
        response.setContentType("text/event-stream;charset=UTF-8"); // ✅ 再保险

        return streamService.streamChat(question, userId != null ? userId.toString() : null);
    }
    /**
     * 提供简单的字符串接口供前端调用
     * @param question 用户问题
     *
     * @return AI生成的回答
     */
    @GetMapping("/chat")
    public Result ask(@RequestParam("question") String question) {
        // 从请求头获取token并解析userId
//        String token = extractToken(request);
//        Long userId = JwtUtils.getUserId(token);
        Long userId = UserHolder.getUserId();

        if (userId == null) {
            return Result.error("无效的token或token已过期");
        }
        
        return dashScopeService.callWithContext(userId.toString(), question);
    }

    /**
     * 获取用户对话上下文
     * @param request HTTP请求对象，用于从请求头获取token
     * @return 对话历史列表
     */
    @GetMapping("/history")
    public Result getHistory(HttpServletRequest request) {
        Long userId = UserHolder.getUserId();
        
        if (userId == null) {
            return Result.error("无效的token或token已过期");
        }
        
        return dashScopeService.getChatHistory(userId.toString());
    }

    /**
     * 清空用户对话上下文
     * @param request HTTP请求对象，用于从请求头获取token
     * @return 操作结果
     */
    @DeleteMapping("/clear")
    public Result clearHistory(HttpServletRequest request) {
        Long userId = UserHolder.getUserId();
        
        if (userId == null) {
            return Result.error("无效的token或token已过期");
        }
        
        return dashScopeService.clearChatHistory(userId.toString());
    }
}