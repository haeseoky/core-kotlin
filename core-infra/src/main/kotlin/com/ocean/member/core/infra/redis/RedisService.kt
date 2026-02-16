package com.ocean.member.core.infra.redis

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
@ConditionalOnProperty(name = ["infra.redis.enabled"], havingValue = "true", matchIfMissing = true)
class RedisService(
    private val redisTemplate: RedisTemplate<String, Any>
) {

    fun set(key: String, value: Any) {
        redisTemplate.opsForValue().set(key, value)
    }

    fun set(key: String, value: Any, timeout: Long, unit: TimeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit)
    }

    fun get(key: String): Any? {
        return redisTemplate.opsForValue().get(key)
    }

    fun delete(key: String): Boolean {
        return redisTemplate.delete(key)
    }

    fun exists(key: String): Boolean {
        return redisTemplate.hasKey(key)
    }

    fun expire(key: String, timeout: Long, unit: TimeUnit): Boolean {
        return redisTemplate.expire(key, timeout, unit)
    }
}
