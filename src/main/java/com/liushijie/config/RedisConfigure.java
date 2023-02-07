package com.liushijie.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * @Author LiuHong1
 * @Date 2022/3/30
 * @Version 1.0
 */
@Configuration
public class RedisConfigure {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // FastJSON 自带序列化器, 不用自己实现
        GenericFastJsonRedisSerializer fastJsonRedisSerializer = new GenericFastJsonRedisSerializer();

        // 生成自定义的 redis 缓存配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // 设置key前的分隔符只要一个引号(默认两个) 修改前 goods:id::12 -> 修改后 goods:id:12
                .computePrefixWith(name -> name + ":")
                // 默认缓存时间 7天 单位秒
                .entryTtl(Duration.ofSeconds(60 * 60 * 24 * 7))
                // 设置value的序列化方式
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(fastJsonRedisSerializer)
                );

        // 将自定义配置作为 redis 缓存操作器的默认配置
        RedisCacheManager redisCacheManager = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .transactionAware()
                .build();

        return redisCacheManager;
    }
}
