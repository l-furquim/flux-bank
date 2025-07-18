package com.fluxbank.gateway_service.domain.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fluxbank.gateway_service.domain.models.UserTokenData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private final ConcurrentHashMap<String, CachedToken> localTokenCache = new ConcurrentHashMap<>();

    private static final String TOKEN_CACHE_PREFIX = "user_token:";
    private static final String USER_SESSIONS_PREFIX = "user_sessions:";
    private static final int LOCAL_CACHE_TTL_SECONDS = 30;

    public TokenService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;

        startCacheCleanupTask();
    }

    public Optional<UserTokenData> getTokenData(String token) {
        try {
            CachedToken cachedToken = localTokenCache.get(token);
            if (cachedToken != null && !cachedToken.isExpired()) {
                log.debug("Token found in local cache for: {}", cachedToken.getData().getUserId());
                return Optional.of(cachedToken.getData());
            }

            String cacheKey = TOKEN_CACHE_PREFIX.concat(token);
            Object cached = redisTemplate.opsForValue().get(cacheKey);

            if (cached != null) {
                UserTokenData tokenData;
                if (cached instanceof UserTokenData) {
                    tokenData = (UserTokenData) cached;
                } else {
                    tokenData = objectMapper.convertValue(cached, UserTokenData.class);
                }

                localTokenCache.put(token, new CachedToken(tokenData, System.currentTimeMillis()));

                log.debug("Token found in Redis cache for: {}", tokenData.getUserId());
                return Optional.of(tokenData);
            }

            return Optional.empty();
        } catch (Exception e) {
            log.error("Error retrieving token data from cache", e);
            return Optional.empty();
        }
    }

    public void removeTokenFromCache(String token) {
        try {
            String cacheKey = TOKEN_CACHE_PREFIX.concat(token);

            localTokenCache.remove(token);

            redisTemplate.delete(cacheKey);

            log.debug("Token removed from both caches");
        } catch (Exception e) {
            log.error("Error removing token from cache", e);
        }
    }

    public void cacheTokenData(String token, UserTokenData tokenData, Duration expiration) {
        try {
            String cacheKey = TOKEN_CACHE_PREFIX.concat(token);

            redisTemplate.opsForValue().set(cacheKey, tokenData, expiration);

            localTokenCache.put(token, new CachedToken(tokenData, System.currentTimeMillis()));

            log.debug("Token cached for user: {}", tokenData.getUserId());
        } catch (Exception e) {
            log.error("Error caching token data", e);
        }
    }

    private void startCacheCleanupTask() {

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            localTokenCache.entrySet().removeIf(entry ->
                    now - entry.getValue().getTimestamp() > LOCAL_CACHE_TTL_SECONDS * 1000);
        }, 60, 60, TimeUnit.SECONDS);
    }

    @Getter
    private static class CachedToken {
        private final UserTokenData data;
        private final long timestamp;

        public CachedToken(UserTokenData data, long timestamp) {
            this.data = data;
            this.timestamp = timestamp;
        }

        public UserTokenData getData() {
            return data;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > LOCAL_CACHE_TTL_SECONDS * 1000;
        }
    }

}
