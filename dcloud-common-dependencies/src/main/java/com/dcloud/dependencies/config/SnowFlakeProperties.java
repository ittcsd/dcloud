package com.dcloud.dependencies.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author dcloud
 * @date 2021/12/28 20:19
 */
@Data
@ConfigurationProperties(prefix = SnowFlakeProperties.PREFIX)
public class SnowFlakeProperties {

    public static final String PREFIX = "snow.flake";

    /**
     * 是否启用
     */
    private boolean enabled = false;

    /**
     * 应用key
     */
    private String serviceKey;

}
