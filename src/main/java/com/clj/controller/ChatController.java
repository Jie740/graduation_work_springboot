//package com.clj.controller;
//
//import com.alibaba.dashscope.common.Message;
//import com.clj.common.result.Result;
//import com.clj.security.util.SecurityUtil;
//import com.clj.service.DashScopeService;
//import com.clj.service.DashScopeStreamService;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * AI 智能助手（所有已登录用户可用）
// */
//@RestController
//@RequestMapping("assistant")
//@RequiredArgsConstructor
//public class ChatController {
//
//    private final DashScopeService dashScopeService;
//    private final DashScopeStreamService streamService;
//
//    @GetMapping(value = "/stream", produces = "text/event-stream;charset=UTF-8")
//    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
//    public SseEmitter stream(String question, HttpServletResponse response) {
//        // 从 SecurityContext 获取 userId
//        Long userId = SecurityUtil.getUserId();
//
//        response.setCharacterEncoding("UTF-8");   // ✅ 核心
//        response.setContentType("text/event-stream;charset=UTF-8"); // ✅ 再保险
//
//        return streamService.streamChat(question, userId != null ? userId.toString() : null);
//    }
//
//    /**
//     * 提供简单的字符串接口供前端调用
//     *
//     * @param question 用户问题
//     * @return AI生成的回答
//     */
//    @GetMapping("/chat")
//    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
//    public Result<Map<String, String>> ask(@RequestParam("question") String question) {
//        Long userId = SecurityUtil.getUserId();
//        return Result.success(dashScopeService.callWithContext(userId.toString(), question));
//    }
//
//    /**
//     * 获取用户对话上下文
//     *
//     * @return 对话历史列表
//     */
//    @GetMapping("/history")
//    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
//    public Result<List<Message>> getHistory() {
//        Long userId = SecurityUtil.getUserId();
//        return Result.success(dashScopeService.getChatHistory(userId.toString()));
//    }
//
//    /**
//     * 清空用户对话上下文
//     *
//     * @return 操作结果
//     */
//    @DeleteMapping("/clear")
//    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN','USER')")
//    public Result<Void> clearHistory() {
//        Long userId = SecurityUtil.getUserId();
//        dashScopeService.clearChatHistory(userId.toString());
//        return Result.success();
//    }
//}
