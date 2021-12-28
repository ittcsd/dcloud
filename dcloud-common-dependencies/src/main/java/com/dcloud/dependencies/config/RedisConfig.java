package com.dcloud.dependencies.config;

import com.dcloud.dependencies.utlils.RedisUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author dcloud
 * @date 2021/12/28 20:19
 */
@Slf4j
@Configuration
public class RedisConfig {

    @Bean
    @ConditionalOnProperty(prefix = "spring.redis", name = "host")
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory factory) {
        log.info("RedisTemplate<String, Object> config init ...");
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @ConditionalOnProperty(prefix = "spring.redis", name = "host")
    public RedisTemplate<String, String> redisTemplateString(@Qualifier("redisConnectionFactory") RedisConnectionFactory factory) {
        log.info("RedisTemplate<String, String> config init ...");
        RedisTemplate<String, String> template = new RedisTemplate<String, String>();
        template.setConnectionFactory(factory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用String
        template.setValueSerializer(stringRedisSerializer);
        // hash的value序列化方式采用String
        template.setHashValueSerializer(stringRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    public RedisUtil redisUtil(RedisTemplate redisTemplate) {
        log.info("RedisUtil config init ...");
        RedisUtil redisUtil = new RedisUtil(redisTemplate);
        return redisUtil;
    }

    /**
     * 添加Redis的RedisMessageListenerContainer用于事件监听
     */
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        log.info("RedisMessageListenerContainer config init ...");
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}
