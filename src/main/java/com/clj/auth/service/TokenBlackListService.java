package com.clj.auth.service;

import com.clj.common.constant.JwtConstants;
import com.clj.security.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Token 黑名单服务
 * 基于 JWT 的 jti（唯一ID）将登出 Token 加入 Redis 黑名单，
 * JwtAuthenticationFilter 校验时会跳过黑名单中的 Token
 *
 * @author ajie
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlackListService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtil jwtUtil;

    /**
     * 异步添加 Token 到黑名单（TTL 与 Token 剩余有效期一致）
     *
     * @param token JWT Token
     */
    @Async
    public void addTokenToBlackList(String token) {
        try {
            Claims claims = jwtUtil.parse(token);
            String jti = claims.getId();
            long remainingSeconds = (claims.getExpiration().getTime() - System.currentTimeMillis()) / 1000;
            if (remainingSeconds <= 0) {
                log.debug("Token 已过期，无需加入黑名单: jti={}", jti);
                return;
            }
            redisTemplate.opsForValue().set(
                    JwtConstants.TOKEN_BLACKLIST_PREFIX + jti,
                    "invalid",
                    remainingSeconds,
                    TimeUnit.SECONDS);
            log.info("Token 已加入黑名单: jti={}", jti);
        } catch (Exception e) {
            log.warn("Token 加入黑名单失败: {}", e.getMessage());
        }
    }
}
