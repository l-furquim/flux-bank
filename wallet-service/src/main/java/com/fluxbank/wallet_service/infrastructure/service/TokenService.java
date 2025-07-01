package com.fluxbank.wallet_service.infrastructure.service;

import com.fluxbank.wallet_service.domain.models.UserTokenData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenService {

    private static final String TOKEN_CACHE_PREFIX = "token:";
    private final RedisTemplate<String, Object> redisTemplate;


    public TokenService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public UserTokenData getTokenData(String token) {
        String cacheKey = TOKEN_CACHE_PREFIX + token;
        Object cached = redisTemplate.opsForValue().get(cacheKey);

        return (UserTokenData) cached;
    }

}
