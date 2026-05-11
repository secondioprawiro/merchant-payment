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
                        .path("/gateway/auth/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://auth-service")
                )
                .route("merchant-service", r -> r
                        .path("/gateway/merchants/**", "/gateway/merchant/**", "/gateway/products/**","/gateway/product/**","/gateway/transaction/**" )
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://merchant-service")
                )

                .build();
    }
}
