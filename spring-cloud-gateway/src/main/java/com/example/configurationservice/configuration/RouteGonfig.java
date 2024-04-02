package com.example.configurationservice.configuration;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.support.RouteMetadataUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Qualifier("routeGonfig")
public class RouteGonfig {


    @Bean
    public WebFilter contextPathWebFilter() {

        return (exchange, chain) -> {
            String remoteAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            exchange.getResponse().getHeaders().set("Client-Ip", remoteAddress);
            exchange.getResponse().getHeaders().set("Server", "Kasra_Haghpanah@Cloud");

            return chain.filter(exchange);
        };
    }

    private String[] getHeaderValues(ServerWebExchange exchange, String headerName) {
        List<String> values = exchange.getRequest().getHeaders().get("headerName");
        if (values == null || values.size() == 0) {
            return new String[0];
        }
        String[] array = new String[values.size()];
        for (int i = 0; i < values.size(); i++) {
            array[i] = values.get(i);
        }
        return array;

    }

    @Bean
    public GlobalFilter customGlobalFilter() {
        return (exchange, chain) -> {
            String remoteAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
            // for setting the request header
            exchange.getRequest().mutate()
                    .header("Client-Ip", remoteAddress)
                    // to detect the user who uses a Proxy Server
                    .header("Forwarded", getHeaderValues(exchange, "Forwarded"))
                    .header("X-Forwarded-For", getHeaderValues(exchange, "X-Forwarded-For"))
                    .header("X-Real-Ip", getHeaderValues(exchange, "X-Real-Ip"));


            // for setting the response header
            exchange.getResponse().getHeaders().set("Client-Ip", remoteAddress);
            exchange.getResponse().getHeaders().set("Server", "Kasra_Haghpanah@Cloud");
            exchange.mutate().response(exchange.getResponse()).build();
            return chain.filter(exchange);
        };
    }

/*    @Bean
    public RouteLocator gateway(SetPathGatewayFilterFactory filterFactory) {

        var route = Route.async()
                .id("test-route")
                .filter(new OrderedGatewayFilter(filterFactory.apply(config -> {
                    config.setTemplate("/customers/");
                }), 1))
                .uri("lb://customers")
                .asyncPredicate(serverWebExchange -> {
                    URI uri = serverWebExchange.getRequest().getURI();
                    String path = uri.getPath();
                    boolean match = path.contains("/customers");
                    return Mono.just(match);
                }).build();

        return () -> Flux.just(route);

    }*/

    @Bean
    public RouteLocator gateway(RouteLocatorBuilder routeLocatorBuilder) {

        return routeLocatorBuilder
                .routes()
                .route("customer-swagger", routeSpec -> { // for swagger
                    return routeSpec.path(
                                    "/v3/api-docs/customer" // path on gateway for customer-doc-api
                            )
                            .filters(fs -> {
                                return fs                 // path on gateway         =>           path on microservice
                                        .rewritePath("/customer/(?<segment>.*)", "/customer/${segment}")
                                        .circuitBreaker(cbc -> cbc
                                                .setName("customerBreaker")
                                                .setFallbackUri("forward:/fb/customer")
                                        );
                            })
                            .metadata(RouteMetadataUtils.RESPONSE_TIMEOUT_ATTR, 10000)
                            .metadata(RouteMetadataUtils.CONNECT_TIMEOUT_ATTR, 10000)
                            .uri("lb://customer");
                })
                .route("websocket_route", routeSpec -> { // for websocket
                    return routeSpec
                            .path("/customer/websocket")// path on gateway
                            .filters(gatewayFilterSpec -> {
                                return gatewayFilterSpec.setPath("/customer/websocket"); // path on microservice
                            })
                            .uri("lb://customer");
                })
                .route("ws_html", routeSpec -> {
                    return routeSpec
                            .path("/ws.html")// path on gateway
                            .filters(gatewayFilterSpec -> {
                                return gatewayFilterSpec.setPath("/ws.html"); // path on microservice
                            })
                            .uri("lb://customer/");
                })

                .route(rs -> rs.path("/default").filters(fs -> fs.filter((exchange, chain) -> {
                                    System.out.println("this is your second chance");
                                    return chain.filter(exchange);
                                }))
                                .uri("https://spring.io/guides")
                )
/*                .route(*//*"route_error",*//* routeSpec -> {
                    return routeSpec.path("/error/**")
                            .filters(gatewayFilterSpec -> {

                                return gatewayFilterSpec
                                        .rewritePath("/error/(?<segment>.*)", "/error/${segment}");

                                        //.retry(5);
*//*                                return gatewayFilterSpec.retry(
                                        new Consumer<RetryGatewayFilterFactory.RetryConfig>() {
                                            @Override
                                            public void accept(RetryGatewayFilterFactory.RetryConfig retryConfig) {
                                                retryConfig.setRetries(5);
                                                //retryConfig.setStatuses(HttpStatus.BAD_REQUEST);
                                            }
                                        }
                                );*//*
                            })
                            .uri("lb://customers/");
                })
                .route(routeSpec -> {// http://localhost:9999/persons?names=1,2,3,kasra
                    return routeSpec
                            .path("/persons")// path in gateway
                            .filters(gatewayFilterSpec -> {// path in microservice
                                return gatewayFilterSpec.setPath("/users");
                            })
                            .uri("lb://customers/");
                })*/

                .route(routeSpec -> {
                    return routeSpec
                            .path("/hello") // path on gateway
                            .and()
                            .host("*.spring.academy").and()
                            .asyncPredicate(serverWebExchange -> {
                                return Mono.just(serverWebExchange.getAttribute("foo"));
                            })
                            .filters(gatewayFilterSpec -> {
                                return gatewayFilterSpec//.setPath("/guides")
                                        .setPath("/courses");  // path on microservice
                            })
                            //.uri("https://spring.io/")
                            .uri("https://spring.academy");
                })
                .route(routeSpec -> {
                    return routeSpec
                            .path("/wiki/**")
                            .filters(gatewayFilterSpec -> {            // path on gateway         =>           path on microservice
                                return gatewayFilterSpec.rewritePath("/wiki/(?<handle>.*)", "/wiki/${handle}");
                            })
                            .uri("https://nl.wikipedia.org");
                })
                .build();
    }
}
