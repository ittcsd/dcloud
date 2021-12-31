package com.dcloud.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @author: dcloud
 * @Email: ittcsd@163.com
 * @Date: 2021/12/31 15:59
 * @Description:
 **/
@Data
@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "user")
public class UserConfiguration {
    private String tokenPrefix;
    private String name;
    private Integer age;
}
