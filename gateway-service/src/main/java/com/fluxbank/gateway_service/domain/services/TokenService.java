package com.fluxbank.gateway_service.domain.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxbank.gateway_service.domain.models.UserTokenData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class TokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOKEN_CACHE_PREFIX = "user_token:";
    private static final String USER_SESSIONS_PREFIX = "user_sessions:";

    public TokenService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public Optional<UserTokenData> getTokenData(String userId) {
        try {
            String cacheKey = TOKEN_CACHE_PREFIX.concat(userId);
            Object cached = redisTemplate.opsForValue().get(cacheKey);

            if (cached != null) {
                if (cached instanceof UserTokenData) {
                    return Optional.of((UserTokenData) cached);
                } else {
                    UserTokenData tokenData = objectMapper.convertValue(cached, UserTokenData.class);
                    return Optional.of(tokenData);
                }
            }

            return Optional.empty();
        } catch (Exception e) {
            log.error("Error retrieving token data from cache", e);
            return Optional.empty();
        }
    }

    public void removeTokenFromCache(String userId) {
        try {
            String cacheKey = TOKEN_CACHE_PREFIX.concat(userId);

            Optional<UserTokenData> tokenData = getTokenData(userId);

            redisTemplate.delete(cacheKey);

            log.debug("Token removed from cache: {}", userId.substring(0, 10) + "...");

        } catch (Exception e) {
            log.error("Error removing token from cache", e);
        }
    }

}
