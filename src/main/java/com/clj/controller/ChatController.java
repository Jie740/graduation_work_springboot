package com.clj.controller;

import com.clj.service.DashScopeService;
import com.clj.service.DashScopeStreamService;
import com.clj.utils.Result;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("assistant")
@RequiredArgsConstructor
public class ChatController {

    private final DashScopeService dashScopeService;
    private final DashScopeStreamService streamService;

    @GetMapping(value = "/stream", produces = "text/event-stream;charset=UTF-8")
    public void stream(@RequestParam String question, HttpServletResponse response) throws IOException {
        streamService.streamChat(question, response);
    }
    /**
     * 提供简单的字符串接口供前端调用
     * @param question 用户问题
     * @return AI生成的回答
     */
    @GetMapping("/chat")
    public Result ask(@RequestParam("question") String question) {
        return dashScopeService.callWithMessage(question);
    }
}