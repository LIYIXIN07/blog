package com.blog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 基于 Redis 的简易滑动窗口限流
 */
@Service
@RequiredArgsConstructor
public class RateLimitService {

    private static final String KEY_PREFIX = "blog:rate:";

    private final ObjectProvider<StringRedisTemplate> redisTemplateProvider;

    public boolean tryAcquire(String key, int maxRequests, Duration window) {
        StringRedisTemplate stringRedisTemplate = redisTemplateProvider.getIfAvailable();
        if (stringRedisTemplate == null) {
            return true;
        }
        try {
            String redisKey = KEY_PREFIX + key;
            Long count = stringRedisTemplate.opsForValue().increment(redisKey);
            if (count != null && count == 1L) {
                stringRedisTemplate.expire(redisKey, window);
            }
            return count != null && count <= maxRequests;
        } catch (Exception ignored) {
            return true;
        }
    }
}
