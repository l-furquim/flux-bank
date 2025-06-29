package com.fluxbank.gateway_service.domain.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxbank.gateway_service.domain.models.UserTokenData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOKEN_CACHE_PREFIX = "token:";
    private static final String USER_SESSIONS_PREFIX = "user_sessions:";

    public TokenService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void cacheTokenData(String token, UserTokenData tokenData, Duration expiration) {
        try {
            String cacheKey = TOKEN_CACHE_PREFIX + token;
            String userSessionsKey = USER_SESSIONS_PREFIX + tokenData.getUserId();

            removeTokenFromCache(token);

            redisTemplate.opsForValue().set(cacheKey, tokenData, expiration);

            redisTemplate.opsForSet().add(userSessionsKey, token);
            redisTemplate.expire(userSessionsKey, expiration);

            log.debug("Token cached successfully for user: {} with expiration: {}",
                    tokenData.getUserId(), expiration);

        } catch (Exception e) {
            log.error("Error caching token data", e);
        }
    }

    public Optional<UserTokenData> getTokenData(String token) {
        try {
            String cacheKey = TOKEN_CACHE_PREFIX + token;
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

    public void removeTokenFromCache(String token) {
        try {
            String cacheKey = TOKEN_CACHE_PREFIX + token;

            Optional<UserTokenData> tokenData = getTokenData(token);

            redisTemplate.delete(cacheKey);

            if (tokenData.isPresent()) {
                String userSessionsKey = USER_SESSIONS_PREFIX + tokenData.get().getUserId();
                redisTemplate.opsForSet().remove(userSessionsKey, token);
            }

            log.debug("Token removed from cache: {}", token.substring(0, 10) + "...");

        } catch (Exception e) {
            log.error("Error removing token from cache", e);
        }
    }

    public void invalidateUserSessions(String userId) {
        try {
            String userSessionsKey = USER_SESSIONS_PREFIX + userId;
            Set<Object> tokens = redisTemplate.opsForSet().members(userSessionsKey);

            if (tokens != null) {
                for (Object token : tokens) {
                    String tokenStr = token.toString();
                    String cacheKey = TOKEN_CACHE_PREFIX + tokenStr;
                    redisTemplate.delete(cacheKey);
                }

                redisTemplate.delete(userSessionsKey);
                log.info("Invalidated {} sessions for user: {}", tokens.size(), userId);
            }

        } catch (Exception e) {
            log.error("Error invalidating user sessions", e);
        }
    }

    public boolean isTokenCached(String token) {
        String cacheKey = TOKEN_CACHE_PREFIX + token;
        return redisTemplate.hasKey(cacheKey);
    }

    public long getTokenTTL(String token) {
        String cacheKey = TOKEN_CACHE_PREFIX + token;
        return redisTemplate.getExpire(cacheKey, TimeUnit.SECONDS);
    }

}
