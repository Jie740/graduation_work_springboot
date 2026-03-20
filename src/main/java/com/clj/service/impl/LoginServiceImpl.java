package com.clj.service.impl;

import com.clj.domain.User;
import com.clj.domain.dto.LoginDto;
import com.clj.service.LoginService;
import com.clj.service.UserService;
import com.clj.utils.JwtUtils;
import com.clj.utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.clj.constants.JwtConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {
    private final UserService userService;
    private final StringRedisTemplate stringRedisTemplate;
    @Override
    public Result login(LoginDto loginDto) {
        User one = this.userService.lambdaQuery().eq(User::getUsername, loginDto.getUsername())
                .eq(User::getPassword, loginDto.getPassword())
                .one();
        if (one==null){
            return Result.error("用户名或密码错误");
        }
        if(one.getStatus()!=1){
            return Result.error("用户被禁用");
        }
        String role = one.getRole();
        String token = JwtUtils.createToken(one.getUserId(), role);
        stringRedisTemplate.opsForValue().set(TOKEN_KEY_PREFIX + role + ":" + one.getUserId(), token, TOKEN_TTL, TimeUnit.HOURS);
        HashMap<String, String> map = new HashMap<>();
        map.put("token",token);
        map.put("role",role);
        return Result.ok(map);
    }

    @Override
    public Result logout(HttpServletRequest request) {
        // 从请求头获取 Token
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        // 解析 Token 获取过期时间（以 JWT 为例）
        Date expiration = null;
        try {
            expiration = JwtUtils.getExpiration(token);
            if(expiration==null){
                return Result.error("token已过期");
            }
        } catch (NullPointerException e) {
            log.debug("token:{},过期时间:{}",token,expiration);
        }
        // 计算 Token 剩余有效期（秒）
        long currentTime = System.currentTimeMillis();
        long remaining = (expiration.getTime() - currentTime)/1000;
        // 将 Token 存入 Redis 黑名单，设置过期时间
        stringRedisTemplate.opsForValue().set(TOKEN_BLACKLIST_KEY_PREFIX + token, "invalid", remaining, TimeUnit.SECONDS);
        return Result.ok();
    }
}
