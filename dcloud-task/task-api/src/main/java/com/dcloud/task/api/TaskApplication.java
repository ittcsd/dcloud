package com.dcloud.task.api;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author: dcloud
 * @Email: ittcsd@163.com
 * @Date: 2021/12/29 17:13
 * @Description:
 **/
@Slf4j
@SpringBootApplication(scanBasePackages = "com.dcloud.**")
@MapperScan(basePackages = {"com.dcloud.**.mapper"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.dcloud.**"})
@EnableHystrix
public class TaskApplication {
    public static void main(String[] args) {
        SpringApplication.run(TaskApplication.class, args);
    }
}
