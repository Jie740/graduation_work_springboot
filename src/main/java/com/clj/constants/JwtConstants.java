package com.clj.constants;

public class JwtConstants {
    public static final String TOKEN_SECRET = "clj_super_secret_key_123456789031231213";// 密钥至少32位
    public static final long EXPIRE_TIME = 2 * 60 * 60 * 1000;  //2小时过期时间
//    public static final long EXPIRE_TIME = 16*60 * 1000;  //16分钟过期时间

    public static final String TOKEN_KEY_PREFIX ="login:token:";

    public static final String TOKEN_BLACKLIST_KEY_PREFIX ="token:blackList:";

    public static final Long TOKEN_TTL=2L; //2小时过期时间
//    public static final Long TOKEN_TTL=16L;  //16分钟过期时间
}
