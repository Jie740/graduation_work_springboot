package com.clj.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

import static com.clj.constants.JwtConstants.TOKEN_BLACKLIST_KEY_PREFIX;


@Component
public class BlacklistInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;
    public BlacklistInterceptor(StringRedisTemplate redisTemplate) {
        this.stringRedisTemplate = redisTemplate;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 1. 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        // 从请求头获取Token（去除"Bearer "前缀）
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return true; // 由后续拦截器处理未携带Token的情况
        }
        String token = authHeader.substring(7); // 提取Token部分

        // 检查Token是否在黑名单中
        Boolean isBlacklisted = stringRedisTemplate.hasKey(TOKEN_BLACKLIST_KEY_PREFIX + token);
        if (Boolean.TRUE.equals(isBlacklisted)) {
            // 设置响应类型为文本，并指定编码为 UTF-8
            response.setContentType("text/plain;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token已失效，请重新登录");
            return false;
        }
        return true;
    }
}