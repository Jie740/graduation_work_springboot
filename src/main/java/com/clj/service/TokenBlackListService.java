package com.clj.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.clj.constants.JwtConstants.TOKEN_BLACKLIST_KEY_PREFIX;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlackListService {
    private final StringRedisTemplate stringRedisTemplate;
    private final long DELAY = 5*1000; //延迟5s更新黑名单

    /**
     * @MethodName:  addTokenToBlackList
     * @author: 作者名
     * @date:  2025-11-19 22:12
     * @Description: 异步添加Token到黑名单
     */
    @Async
    public void addTokenToBlackList(String oldToken) {
        // 添加Token到黑名单
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            // 线程被中断，通常发生在应用关闭时
            Thread.currentThread().interrupt();
            log.debug("添加Token到黑名单失败");
        }
        stringRedisTemplate.opsForValue().set(TOKEN_BLACKLIST_KEY_PREFIX + oldToken, "invalid", DELAY, TimeUnit.SECONDS);
        System.out.println("添加"+oldToken+"到黑名单成功");
    }
}
