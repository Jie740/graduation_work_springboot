package com.clj.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码编码器配置
 *
 * @author ajie
 */
@Configuration
public class PasswordConfig {

    /**
     * BCrypt 密码编码器
     * 注册时加密存储，登录时校验对比
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
