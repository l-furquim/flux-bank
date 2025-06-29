package com.fluxbank.gateway_service.config;

import com.fluxbank.gateway_service.domain.filters.UserContextGatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final UserContextGatewayFilter userContextFilter;

    public GatewayConfig(UserContextGatewayFilter userContextFilter) {
        this.userContextFilter = userContextFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Rotas protegidas

                // Rotas públicas - sem filtro de autenticação
                .route("auth-service", r -> r
                        .path("/users/register", "/users/login",
                                "/users/forgot-password", "/users/reset-password")
                        .uri("lb://user-service"))

                .route("health-check", r -> r
                        .path("/health", "/actuator/**")
                        .uri("lb://user-service"))

                .build();
    }
}