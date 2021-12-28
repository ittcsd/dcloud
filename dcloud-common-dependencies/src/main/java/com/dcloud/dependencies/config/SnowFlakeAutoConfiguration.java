package com.dcloud.dependencies.config;

import com.dcloud.dependencies.utlils.RedisUtil;
import com.dcloud.dependencies.utlils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author dcloud
 * @date 2021/12/28 20:19
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(SnowFlakeProperties.class)
public class SnowFlakeAutoConfiguration {

    @Resource
    private RedisUtil redisUtil;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = SnowFlakeProperties.PREFIX, value = "enabled", havingValue = "true")
    public SnowflakeIdWorker snowflakeIdWorker(SnowFlakeProperties snowFlakeProperties) {
        SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(snowFlakeProperties, redisUtil);
        return snowflakeIdWorker;
    }

}
