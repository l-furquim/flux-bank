package com.fluxbank.gateway_service.domain.filters;

import com.fluxbank.gateway_service.domain.models.UserTokenData;
import com.fluxbank.gateway_service.domain.services.TokenService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import java.time.Instant;
import java.util.*;

@Component
@Slf4j
public class UserContextGatewayFilter implements GatewayFilter, Ordered {

    private final TokenService tokenService;
    private final Counter approvedRequests;
    private final Counter notApprovedRequests;

    public UserContextGatewayFilter(TokenService tokenService, MeterRegistry registry) {
        this.tokenService = tokenService;
        this.approvedRequests = Counter.builder("approved_requests")
                .description("Numero de requisições aprovadas pelo gateway")
                .register(registry);

        this.notApprovedRequests = Counter.builder("not_approved_requests")
                .description("Numero de requisições não aprovadas pelo gateway")
                .register(registry);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        HttpCookie sessionCookie = exchange.getRequest()
                .getCookies()
                .getFirst("SESSION");

        if (sessionCookie == null) {
            return handleUnauthorized(exchange, "Usuário não autenticado.");
        }

        String token = sessionCookie.getValue();

        return processTokenAndContinue(exchange, chain, token, request);
    }

    private Mono<Void> processTokenAndContinue(ServerWebExchange exchange, GatewayFilterChain chain,
                                               String token, ServerHttpRequest request) {

        Optional<UserTokenData> cachedTokenData = tokenService.getTokenData(token);

        if (cachedTokenData.isPresent()) {
            UserTokenData tokenData = cachedTokenData.get();

            if (tokenData.isExpired()) {
                tokenService.removeTokenFromCache(token);
                return handleUnauthorized(exchange, "Sessão expirada");
            }

            log.debug("Using cached token data for user: {}", tokenData.getUserId());
            approvedRequests.increment();
            return continueWithTokenData(exchange, chain, tokenData, token, request, false);
        }

        return handleUnauthorized(exchange, "Sessão inválida");

//        try {
//            Jwt jwt = jwtDecoder.decode(token);
//            UserTokenData tokenData = extractTokenDataFromJwt(jwt);
//
//            Duration expiration = Duration.between(Instant.now(), tokenData.getExpiresAt());
//
//            if (expiration.isNegative() || expiration.isZero()) {
//
//                return handleUnauthorized(exchange, "Token expirado");
//            }
//
//            tokenService.cacheTokenData(token, tokenData, expiration);
//
//            log.debug("Token validated and cached for user: {}", tokenData.getUserId());
//            approvedRequests.increment();
//            return continueWithTokenData(exchange, chain, tokenData, token, request, true);
//
//        } catch (JwtException e) {
//            log.warn("Token JWT inválido: {}", e.getMessage());
//            return handleUnauthorized(exchange, "Token JWT inválido");
//        }
    }

    private Mono<Void> continueWithTokenData(ServerWebExchange exchange, GatewayFilterChain chain,
                                             UserTokenData tokenData, String token,
                                             ServerHttpRequest request, boolean newlyValidated) {

        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Id", tokenData.getUserId())
                .header("X-User-Email", tokenData.getEmail())
                .header("X-Device-Id", tokenData.getDeviceId() != null ? tokenData.getDeviceId() : "unknown")
                .header("X-Request-Id", generateRequestId())
                .header("X-Gateway-Timestamp", String.valueOf(System.currentTimeMillis()))
                .header("X-Token-Cached", newlyValidated ? "false" : "true")
                .build();

        logRequest(tokenData.getUserId(), request.getPath().value(),
                request.getMethod().name(), newlyValidated);

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private void logRequest(String userId, String path, String method, boolean newlyValidated) {
        log.info("Request - UserId: {}, Path: {}, Method: {}, TokenCached: {}",
                userId, path, method, !newlyValidated);
    }

    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }


    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String message) {
        notApprovedRequests.increment();

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");

        String body = String.format("{\"error\":\"Unauthorized\",\"message\":\"%s\",\"timestamp\":\"%s\"}",
                message, Instant.now());
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}