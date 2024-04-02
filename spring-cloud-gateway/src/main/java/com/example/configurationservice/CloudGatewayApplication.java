package com.example.configurationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
// https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway/gatewayfilter-factories/requestratelimiter-factory.html#:~:text=The%20Redis%20RateLimiter&text=The%20algorithm%20used%20is%20the,the%20token%20bucket%20is%20filled.
// https://docs.spring.io/spring-cloud-gateway/reference/4.1-SNAPSHOT/spring-cloud-gateway/sharing-routes.html => for launching multiple instances of Spring Cloud Gateway
// https://github.com/spring-cloud/spring-cloud-gateway/issues/1029 => for launching multiple instances of Spring Cloud Gateway
public class CloudGatewayApplication {
    // https://medium.com/@oguz.topal/central-swagger-in-spring-cloud-gateway-697a1c37b03d
    public static void main(String[] args) {
        SpringApplication.run(CloudGatewayApplication.class, args);
    }

}
