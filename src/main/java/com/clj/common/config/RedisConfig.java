package com.clj.common.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置（自定义序列化方式，避免乱码）
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(
            RedisConnectionFactory factory){

        RedisTemplate<String,Object> template =
                new RedisTemplate<>();

        template.setConnectionFactory(factory);


        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer();


        template.setKeySerializer(
                new StringRedisSerializer()
        );


        template.setValueSerializer(serializer);


        template.afterPropertiesSet();

        return template;
    }
}
