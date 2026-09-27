package com.fluxbank.commons_web.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.Duration;

@RequiredArgsConstructor
public class IdempotencyFilter extends OncePerRequestFilter {

    private static final String HEADER_NAME = "Idempotency-Key";
    private static final Duration TTL = Duration.ofHours(24);

    private final StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String idempotencyKey = request.getHeader(HEADER_NAME);

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        String redisKey = "idempotency:" + idempotencyKey;
        String cachedResponse = redisTemplate.opsForValue().get(redisKey);

        if (cachedResponse != null) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.getWriter().write(cachedResponse);
            return;
        }

        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(request, wrappedResponse);

        if (wrappedResponse.getStatus() < 300) {
            String body = new String(wrappedResponse.getContentAsByteArray());
            redisTemplate.opsForValue().set(redisKey, body, TTL);
        }

        wrappedResponse.copyBodyToResponse();
    }
}