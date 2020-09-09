package com.planet.token.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CacheServiceImpl implements CacheService {

    private final StringRedisTemplate redisTemplate;
    private static final String TOKEN_MAP = "PLANET_TOKEN_MAP";
    private static final String TOKEN_CACHE_KEY_PREFIX = "PLANET_TOKEN_KEY:";

    public CacheServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getTokenByAppId(String appId) {
        boolean hasKey = redisTemplate.opsForHash().hasKey(TOKEN_MAP, TOKEN_CACHE_KEY_PREFIX + appId);
        if (hasKey) {
            Object val = redisTemplate.opsForHash().get(TOKEN_MAP, TOKEN_CACHE_KEY_PREFIX + appId);
            if (val != null) {
                return val.toString();
            }
        }
        return null;
    }

    @Override
    public void refreshToken(String appId, String token) {
        redisTemplate.opsForHash().put(TOKEN_MAP, TOKEN_CACHE_KEY_PREFIX + appId, token);
    }

}
