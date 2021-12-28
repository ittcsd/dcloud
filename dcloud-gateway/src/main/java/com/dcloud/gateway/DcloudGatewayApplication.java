package com.dcloud.gateway;

//import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.dcloud.**")
//@MapperScan(basePackages = {"com.dcloud.**.mapper"})
@EnableDiscoveryClient
public class DcloudGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(DcloudGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("path_route",r->r.path("/user-api/**")
                .uri("http://www.1688.com/"))
                .build();
    }
}
