package com.fluxbank.commons_web.config;

import com.fluxbank.commons_web.filters.IdempotencyFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

@AutoConfiguration
@AutoConfigureAfter(name = "org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration")
@ConditionalOnClass(name = "org.springframework.data.redis.core.StringRedisTemplate")
@ConditionalOnBean(StringRedisTemplate.class)
@ConditionalOnProperty(prefix = "fluxbank.idempotency", name = "enabled", havingValue = "true")
public class FluxIdempotencyAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    IdempotencyFilter idempotencyFilter(StringRedisTemplate redisTemplate) {
        return new IdempotencyFilter(redisTemplate);
    }
}
