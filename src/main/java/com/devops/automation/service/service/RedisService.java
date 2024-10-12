package com.devops.automation.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void setCacheList(String key, String value) {
        redisTemplate.opsForList().rightPush(key, value);
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
    }

    public List<Object> getCacheList(String key) {
        return redisTemplate.opsForList().range(key, 0, 50);
    }
}
