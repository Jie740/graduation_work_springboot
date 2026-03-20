package com.clj.interceptor;



import com.clj.service.TokenBlackListService;
import com.clj.utils.JwtUtils;
import com.clj.utils.UserHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.clj.constants.JwtConstants.TOKEN_KEY_PREFIX;
import static com.clj.constants.JwtConstants.TOKEN_TTL;


@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    private final TokenBlackListService tokenBlackListService;

    public LoginInterceptor(StringRedisTemplate redisTemplate, TokenBlackListService tokenBlackListService) {
        this.stringRedisTemplate = redisTemplate;
        this.tokenBlackListService = tokenBlackListService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 1. 预检请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            response.setContentType("text/plain;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("请先登录");
            return false;
        }
        String token = header.substring(7); // 去除 "Bearer "


        // 2. 解析 token
        Long userId = JwtUtils.getUserId(token);
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // 设置响应类型为文本，并指定编码为 UTF-8
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("token已失效，请重新登录");
            return false;
        }

        // 3. 验证 token 是否和 Redis 中一致
        String role=JwtUtils.getClaim(token, "role");
        String redisKey = TOKEN_KEY_PREFIX + role + ":" + userId;
        String redisToken = stringRedisTemplate.opsForValue().get(redisKey);
        if (redisToken == null || !redisToken.equals(token)) {
            // 设置响应类型为文本，并指定编码为 UTF-8
            response.setContentType("text/plain;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            System.out.println("与redis不一致");
            response.getWriter().write("Token已失效，请重新登录");
            return false;
        }

        // ✅ 4. 判断 token 是否快过期（剩余 < 15分钟）
        Date expiration = JwtUtils.getExpiration(token);
        long currentTime = System.currentTimeMillis();
        long remaining;
        if(expiration==null){
            remaining=0;
        }else {
            remaining = (expiration.getTime() - currentTime)/1000;
        }

//        Long expire = stringRedisTemplate.getExpire(redisKey);
//        if(expire!=null&&expire/60<15){
        if (remaining>0&&remaining < 15 * 60) { // 剩余少于15分钟
            // 生成新 token
            String newToken = JwtUtils.createToken(userId,role);

            // 替换 Redis 中 token
            stringRedisTemplate.opsForValue().set(redisKey, newToken, TOKEN_TTL, TimeUnit.HOURS);
//            stringRedisTemplate.opsForValue().set(redisKey, newToken, TOKEN_TTL, TimeUnit.MINUTES);

            //将新token保存到响应头中，浏览器替换
            response.setHeader("Authorization", newToken);
            // 4. 异步、延时地将旧 token 加入黑名单
            // 延时时间可以设置为一个略大于新旧 token 交替的安全窗口
            tokenBlackListService.addTokenToBlackList(token);
        } else {
            // 刷新 Redis token 有效期（滑动过期）
            stringRedisTemplate.expire(redisKey, TOKEN_TTL, TimeUnit.HOURS);
//            stringRedisTemplate.expire(redisKey, TOKEN_TTL, TimeUnit.MINUTES);
        }

        // 5. 保存用户ID
        UserHolder.setUserId(userId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求完成清理 ThreadLocal，防内存泄漏
        UserHolder.remove();
    }
}
