package com.example.configurationservice.configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Objects;

@Configuration
public class SwaggerConfig {

    //@Bean
    //@DependsOn("routeGonfig")
    public CommandLineRunner openApiGroups(
            RouteDefinitionLocator locator,
            RouteLocatorBuilder routeLocatorBuilder,
            SwaggerUiConfigParameters swaggerUiParameters) {
        return args -> {


            Objects.requireNonNull(locator
                            .getRouteDefinitions().collectList().block())
                    .stream()
                    .map(RouteDefinition::getId)
                    .filter(id -> {
                        return id.matches(".*-swagger");
                    })
                    .map(id -> id.replace("-swagger", ""))
                    .forEach(swaggerUiParameters::addGroup);
        };
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> {
            factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                    .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                    .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(7)).build())
                    .circuitBreakerConfig(CircuitBreakerConfig.custom().failureRateThreshold(10)
                            .slowCallRateThreshold(5)
                            .slowCallDurationThreshold(Duration.ofSeconds(3)).build()).build());
        };
    }

/*    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("/**"));
        //configuration.setAllowedOrigins(Arrays.asList("http://localhost"));
        configuration.setAllowedMethods(Arrays.asList("GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("Content-Type, api_key, Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrfSpec -> {
                    csrfSpec.disable();
                })
                .formLogin(formLoginSpec -> {
                    formLoginSpec.disable();
                })
                .httpBasic(httpBasicSpec -> {
                    httpBasicSpec.disable();
                })
                .cors(corsSpec -> {
                    corsSpec.configurationSource(corsConfigurationSource());
                })
                .authorizeExchange(authorizeExchangeSpec -> {
                    authorizeExchangeSpec.pathMatchers("/**").permitAll();
                })
                .build();
    }*/



    //@Bean
//    public GroupedOpenApi customerApi() {
//        return GroupedOpenApi.builder()
//                .pathsToMatch("/customer/**")
//                .group("customer")
//                .build();
//    }

}
