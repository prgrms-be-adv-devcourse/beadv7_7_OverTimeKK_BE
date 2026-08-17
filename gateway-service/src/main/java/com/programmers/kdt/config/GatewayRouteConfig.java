package com.programmers.kdt.config;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicate;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * properties 기반 spring.cloud.gateway.server.webmvc.routes 대신 Java RouterFunction으로 라우트를 정의한다.
 */
@Configuration
public class GatewayRouteConfig {

    @Value("${user-service.url}")
    private String userServiceUrl;

    @Value("${order-service.url}")
    private String orderServiceUrl;

    @Value("${performance-service.url}")
    private String performanceServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes() {
        RequestPredicate orderServicePaths = path("/api/order/**")
                .or(path("/api/payments/**"))
                .or(path("/api/points/**"))
                .or(path("/api/settlements/**"));

        RequestPredicate performanceServicePaths = path("/api/performances/**")
                .or(path("/api/venues/**"))
                .or(path("/api/halls/**"))
                .or(path("/api/tickets/**"))
                .or(path("/api/standby/**"));

        return route("user-service")
                .route(path("/api/users/**"), http())
                .before(uri(userServiceUrl))
                .build()
            .and(route("order-service")
                .route(orderServicePaths, http())
                .before(uri(orderServiceUrl))
                .build())
            .and(route("performance-service")
                .route(performanceServicePaths, http())
                .before(uri(performanceServiceUrl))
                .build());
    }
}
