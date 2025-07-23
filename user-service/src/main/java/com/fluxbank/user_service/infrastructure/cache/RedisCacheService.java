package com.fluxbank.user_service.infrastructure.cache;

import com.fluxbank.user_service.domain.exceptions.CacheDataException;
import com.fluxbank.user_service.interfaces.dto.UserTokenData;
import com.fluxbank.user_service.domain.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class RedisCacheService implements CacheService {

    private static final String USER_SESSIONS_PREFIX = "user_sessions:";
    private static final String TOKEN_PREFIX = "user_token:";
    private static final Duration TOKEN_EXPIRATION = Duration.ofHours(1);

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    @Override
    public void cacheToken(String token, UserTokenData tokenData) {
        try {
            String userSessionsKey = USER_SESSIONS_PREFIX + tokenData.getUserId();
            String tokenKey = TOKEN_PREFIX + token;


            boolean isSessionAlreadyActive = redisTemplate.hasKey(userSessionsKey);

            if(isSessionAlreadyActive){
                this.removeTokenFromCache(userSessionsKey);
            }

            redisTemplate.opsForValue().set(tokenKey, tokenData);
            redisTemplate.expire(tokenKey, TOKEN_EXPIRATION);

            redisTemplate.opsForValue().set(userSessionsKey, token);
            redisTemplate.expire(userSessionsKey, TOKEN_EXPIRATION);

            log.debug("Token cached successfully for user: {} with expiration: {}",
                    tokenData.getUserId(), TOKEN_EXPIRATION);

        } catch (Exception e) {
            log.error("Error while caching the token: {}", e.getMessage());
            throw new CacheDataException("Error while caching the data: " + e.getMessage());
        }
    }

    public void removeTokenFromCache(String sessionKey) {
        try {

            String token = (String) redisTemplate.opsForValue().get(sessionKey);

            String tokenKey = TOKEN_PREFIX + token;

            redisTemplate.delete(sessionKey);
            redisTemplate.delete(tokenKey);

            log.debug("Token removed from cache: {}", tokenKey.substring(0, 10) + "...");

        } catch (Exception e) {
            log.error("Error removing token from cache", e);
            throw new CacheDataException("Error while remove the caching data: " + e.getMessage());
        }
    }
}
