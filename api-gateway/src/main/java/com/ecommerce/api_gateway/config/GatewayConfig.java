package com.ecommerce.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                //  CACHE SERVICE - CORRECT REWRITE PATH
                .route("cache-service", r -> r
                        .path("/cache/**")
                        .filters(f -> f.rewritePath("/cache/(?<path>.*)", "/api/cache/${path}"))
                        .uri("http://localhost:8084"))

                .route("cache-service-post", r -> r
                        .path("/cache/**")
                        .and()
                        .method("POST", "PUT", "DELETE")
                        .filters(f -> f.rewritePath("/cache/(?<path>.*)", "/api/cache/${path}"))
                        .uri("http://localhost:8084"))

                //  ORIGINAL SERVICES
                .route("customer-service", r -> r
                        .path("/api/customers/**")
                        .uri("lb://customer-service"))

                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .uri("lb://order-service"))

                .route("product-service", r -> r
                        .path("/api/products/**")
                        .uri("lb://product-service"))

                .build();
    }
}