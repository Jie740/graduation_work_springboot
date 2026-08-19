package com.clj.security.util;

import com.clj.security.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 工具类
 * 负责 Token 的生成、解析与验证
 *
 * @author ajie
 */
@Slf4j
@Component
public class JwtUtil {

    /**
     * JWT 签名密钥（至少256位，对应HMAC-SHA256）
     */
    private static final String SECRET = "exam-system-jwt-secret-key-must-be-at-least-256-bits-for-hs256";

    /**
     * Token 有效期 24小时（毫秒）
     */
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000L;

    /**
     * 生成签名密钥
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 创建 JWT Token
     *
     * @param loginUser 登录用户
     * @return JWT Token 字符串
     */
    public String createToken(LoginUser loginUser) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_MS);

        return Jwts.builder()
                // 签发者
                .setIssuer("exam-system")
                // 面向用户
                .setSubject(loginUser.getUsername())
                // 签发时间
                .setIssuedAt(now)
                // 过期时间
                .setExpiration(expiration)
                // JWT唯一ID（用于Redis黑名单）
                .setId(UUID.randomUUID().toString().replace("-", ""))
                // 自定义字段：用户ID
                .claim("userId", loginUser.getUserId())
                // 自定义字段：用户名
                .claim("username", loginUser.getUsername())
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 解析 JWT Token
     *
     * @param token JWT Token 字符串
     * @return Claims 声明体
     */
    public Claims parse(String token) {
        return Jwts.parser()
                .setSigningKey(getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 判断 Token 是否过期
     *
     * @param token JWT Token 字符串
     * @return true-已过期 false-未过期
     */
    public boolean isExpired(String token) {
        try {
            Claims claims = parse(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 从 Token 中获取用户ID
     *
     * @param token JWT Token 字符串
     * @return 用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parse(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从 Token 中获取用户名
     *
     * @param token JWT Token 字符串
     * @return 用户名
     */
    public String getUsername(String token) {
        Claims claims = parse(token);
        return claims.getSubject();
    }

    /**
     * 从 Token 中获取 JTI（唯一标识）
     *
     * @param token JWT Token 字符串
     * @return JTI
     */
    public String getJti(String token) {
        Claims claims = parse(token);
        return claims.getId();
    }
}
