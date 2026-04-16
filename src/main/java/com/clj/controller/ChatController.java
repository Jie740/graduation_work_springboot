package com.clj.controller;

import com.clj.service.DashScopeService;
import com.clj.service.DashScopeStreamService;
import com.clj.utils.JwtUtils;
import com.clj.utils.Result;
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
        String token = extractToken(request);
        Long userId = JwtUtils.getUserId(token);
        
        response.setCharacterEncoding("UTF-8");   // ✅ 核心
        response.setContentType("text/event-stream;charset=UTF-8"); // ✅ 再保险

        return streamService.streamChat(question, userId != null ? userId.toString() : null);
    }
    /**
     * 提供简单的字符串接口供前端调用
     * @param question 用户问题
     * @param request HTTP请求对象，用于从请求头获取token
     *
     * @return AI生成的回答
     */
    @GetMapping("/chat")
    public Result ask(@RequestParam("question") String question,
                      HttpServletRequest request) {
        // 从请求头获取token并解析userId
        String token = extractToken(request);
        Long userId = JwtUtils.getUserId(token);
        
        if (userId == null) {
            return Result.error("无效的token或token已过期");
        }
        
        return dashScopeService.callWithContext(userId.toString(), question);
    }
    
    /**
     * 从请求头中提取token
     * @param request HTTP请求对象
     * @return token字符串
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}