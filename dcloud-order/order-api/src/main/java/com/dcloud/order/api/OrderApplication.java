package com.dcloud.order.api;


import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Slf4j
@SpringBootApplication(scanBasePackages = "com.dcloud.**")
@MapperScan(basePackages = {"com.dcloud.**.mapper"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.dcloud.**"})
@EnableHystrix
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
