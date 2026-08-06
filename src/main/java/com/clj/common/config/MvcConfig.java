package com.clj.common.config;

import com.clj.common.interceptor.BlacklistInterceptor;
import com.clj.common.interceptor.LoginInterceptor;
import com.clj.service.TokenBlackListService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {

    private final StringRedisTemplate stringRedisTemplate;
    private final TokenBlackListService tokenBlackListService;
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new BlacklistInterceptor(stringRedisTemplate))
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/logout")
                .order(0);
        registry.addInterceptor(new LoginInterceptor(stringRedisTemplate,tokenBlackListService))
                .addPathPatterns("/**")
                .excludePathPatterns( "/login", "/logout")
                .order(1);
    }
}
