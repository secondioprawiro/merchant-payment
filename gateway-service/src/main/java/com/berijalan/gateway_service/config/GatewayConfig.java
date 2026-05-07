package com.berijalan.gateway_service.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/auth/**")
                        .uri("lb://auth-service")
                )
                .route("user-wallet-service", r -> r
                        .path("/users/**", "/wallets/**")
                        .uri("lb://user-wallet-service")
                )
                .route("transaction-service", r -> r
                        .path("/transactions/**", "/admin/**")
                        .uri("lb://transaction-service")
                )
                .build();
    }
}
