package com.clj.common.constant;

/**
 * 系统级常量
 */
public class SystemConstant {

    private SystemConstant() {
    }

    /**
     * JWT Token 请求头名称
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * JWT Token 前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * JWT 签名密钥（生产环境应使用配置中心管理）
     */
    public static final String JWT_SECRET = "exam-system-jwt-secret-key-must-be-at-least-256-bits";

    /**
     * JWT Token 有效期（毫秒）- 24小时
     */
    public static final long JWT_EXPIRATION = 60 * 60 * 24 * 1000L;

    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大分页大小
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 超级管理员角色编码
     */
    public static final String SUPER_ADMIN = "ROLE_ADMIN";
}
