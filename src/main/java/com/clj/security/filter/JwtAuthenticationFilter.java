package com.clj.security.filter;

import com.clj.common.constant.SystemConstant;
import com.clj.security.LoginUser;
import com.clj.security.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 认证过滤器
 * 每个请求进入时解析 Token，设置 SecurityContext
 *
 * @author ajie
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Redis JWT 黑名单前缀
     */
    private static final String JWT_BLACKLIST_PREFIX = "jwt:blacklist:";

    /**
     * Redis 用户权限缓存前缀
     */
    private static final String USER_PERMISSION_PREFIX = "user:permission:";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String tokenHeader = request.getHeader(SystemConstant.TOKEN_HEADER);

        // 没有 Token，直接放行（由 Spring Security 权限配置决定是否拦截）
        if (tokenHeader == null || !tokenHeader.startsWith(SystemConstant.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        String token = tokenHeader.substring(SystemConstant.TOKEN_PREFIX.length());

        try {
            // 1. 解析 JWT
            Claims claims = jwtUtil.parse(token);
            Long userId = claims.get("userId", Long.class);
            String username = claims.getSubject();
            String jti = claims.getId();

            // 2. 检查 Token 是否在黑名单中
            String blacklistKey = JWT_BLACKLIST_PREFIX + jti;
            Boolean isBlacklisted = redisTemplate.hasKey(blacklistKey);
            if (Boolean.TRUE.equals(isBlacklisted)) {
                log.warn("Token 已被加入黑名单: {}", jti);
                chain.doFilter(request, response);
                return;
            }

            // 3. 尝试从 Redis 获取用户权限缓存
            String permissionKey = USER_PERMISSION_PREFIX + userId;
            @SuppressWarnings("unchecked")
            List<String> permissions = (List<String>) redisTemplate.opsForValue().get(permissionKey);

            // 4. 构建 LoginUser 并设置 SecurityContext
            LoginUser loginUser = new LoginUser();
            loginUser.setUserId(userId);
            loginUser.setUsername(username);
            loginUser.setPermissions(permissions);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            loginUser,
                            null,
                            loginUser.getAuthorities()
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("JWT 认证成功: userId={}, username={}", userId, username);

        } catch (ExpiredJwtException e) {
            log.warn("Token 已过期: {}", e.getMessage());
        } catch (MalformedJwtException | SignatureException e) {
            log.warn("Token 无效: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT 认证异常: {}", e.getMessage());
        }

        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 对登录接口不执行 JWT 过滤器
        String path = request.getServletPath();
        return "/api/auth/login".equals(path) || "/api/auth/register".equals(path);
    }
}
