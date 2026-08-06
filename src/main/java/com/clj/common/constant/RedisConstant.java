package com.clj.common.constant;

/**
 * Redis 相关常量
 */
public class RedisConstant {

    private RedisConstant() {
    }

    /**
     * 用户 Token 前缀
     */
    public static final String USER_TOKEN_PREFIX = "user:token:";

    /**
     * 用户信息缓存前缀
     */
    public static final String USER_INFO_PREFIX = "user:info:";

    /**
     * 用户权限缓存前缀
     */
    public static final String USER_PERMISSION_PREFIX = "user:permission:";

    /**
     * 验证码前缀
     */
    public static final String CAPTCHA_PREFIX = "captcha:";

    /**
     * Token 过期时间（秒）- 24小时
     */
    public static final long TOKEN_EXPIRE_SECONDS = 60 * 60 * 24;

    /**
     * 验证码过期时间（秒）- 5分钟
     */
    public static final long CAPTCHA_EXPIRE_SECONDS = 60 * 5;
}
