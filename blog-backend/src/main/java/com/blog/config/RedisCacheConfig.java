package com.blog.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class RedisCacheConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        GenericJackson2JsonRedisSerializer serializer = jsonSerializer();
        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .disableCachingNullValues()
                .entryTtl(Duration.ofMinutes(30));

        return builder -> builder
                .cacheDefaults(base)
                .withCacheConfiguration(CacheNames.SETTINGS, base.entryTtl(Duration.ofHours(1)))
                .withCacheConfiguration(CacheNames.ABOUT, base.entryTtl(Duration.ofHours(1)))
                .withCacheConfiguration(CacheNames.FRIEND_LINKS, base.entryTtl(Duration.ofHours(1)))
                .withCacheConfiguration(CacheNames.CATEGORIES, base.entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration(CacheNames.TAGS, base.entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration(CacheNames.ARTICLE_LIST, base.entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration(CacheNames.ARTICLE_SEARCH, base.entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration(CacheNames.ARTICLE_LATEST, base.entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration(CacheNames.ARTICLE_HOT, base.entryTtl(Duration.ofMinutes(5)))
                .withCacheConfiguration(CacheNames.ARTICLE_RANDOM, base.entryTtl(Duration.ofMinutes(2)))
                .withCacheConfiguration(CacheNames.ARTICLE_ARCHIVES, base.entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration(CacheNames.SITE_STATS, base.entryTtl(Duration.ofMinutes(3)));
    }

    private GenericJackson2JsonRedisSerializer jsonSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        return new GenericJackson2JsonRedisSerializer(mapper);
    }
}
