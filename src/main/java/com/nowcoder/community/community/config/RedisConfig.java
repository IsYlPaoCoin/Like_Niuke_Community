package com.nowcoder.community.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * ClassName: RedisConfig
 * Package: com.nowcoder.community.community.config
 * Description:
 *
 * @Author 杨理
 * @Create 2024/4/7 11:42
 * @Version 1.0
 */

@Configuration
public class RedisConfig {

    // 需要把 哪个 对象装配到 容器当中
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 这样就具备了  访问数据库的能力
        template.setConnectionFactory(factory);

        // 设置数据 序列化的方式
        // 1 设置 key 的序列化 方式
        template.setKeySerializer(RedisSerializer.string());
        // 2 设置 value 的序列化 方式
        template.setValueSerializer(RedisSerializer.json());
        // 3 设置 hash 的key 序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        // 4 设置 hash 的value 序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();
        return template;
    }
}
