package com.clj.auth.controller;

import com.clj.auth.service.AuthService;
import com.clj.common.result.Result;
import com.clj.domain.dto.LoginDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 登录接口（前端请求路径为 /login、/logout）
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody LoginDto loginDto) {
        return authService.login(loginDto);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        return authService.logout(request);
    }
}
