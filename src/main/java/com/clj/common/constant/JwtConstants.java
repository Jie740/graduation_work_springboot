package com.clj.common.constant;

/**
 * JWT 相关常量（Spring Security + jti 黑名单体系）
 */
public class JwtConstants {

    /**
     * Redis Token 黑名单前缀（key = jti）
     */
    public static final String TOKEN_BLACKLIST_PREFIX = "jwt:blacklist:";
}
