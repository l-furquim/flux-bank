package com.fluxbank.gateway_service.config;

import com.fluxbank.gateway_service.domain.filters.UserContextGatewayFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final UserContextGatewayFilter userContextFilter;

    @Value("${gateway.routes.user-service}")
    private String userRoute;

    @Value("${gateway.routes.wallet-service}")
    private String walletRoute;

    public GatewayConfig(UserContextGatewayFilter userContextFilter) {
        this.userContextFilter = userContextFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // Rotas protegidas
                .route("wallet-service", r -> r
                        .path("/api/v1/wallets/create", "/api/v1/wallets/deposit",
                                "/api/v1/wallets/balance", "/api/v1/wallets/withdraw",
                                "/api/v1/wallets/limits")
                        .filters(f -> f.filter(userContextFilter))
                        .uri(walletRoute))

                // Rotas públicas - sem filtro de autenticação
                .route("user-service", r -> r
                        .path("/api/v1/users/register", "/api/v1/users/auth",
                                "/api/v1/users/forgot-password", "/api/v1/users/reset-password")
                        .uri(userRoute))

                .route("health-check", r -> r
                        .path("/health", "/actuator/**")
                        .uri("lb://user-service"))

                .build();
    }
}