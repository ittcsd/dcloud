package com.dcloud.dependencies.config;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
/**
 * @author: dcloud
 * @Email: ittcsd@163.com
 * @Date: 2021/12/28 11:29
 * @Description: swagger配置类
 **/
@Configuration //配置类
@EnableSwagger2// 开启Swagger2的自动配置
@Slf4j
@Profile({"dev", "test"})
public class SwaggerConfig {
    /**
     * 配置了Swagger 的Docket的bean实例,扫描接口的位置
     * .apis
     *   RequestHandlerSelectors 配置swagger扫描接口的方式
     *      basePackage() 指定要扫描哪些包
     *      any() 全部都扫描
     *      none() 全部不扫描
     *      withClassAnnotation() 扫描类上的注解 参数是一个注解的反射对象
     *      withMethodAnnotation() 扫描包上的注解
     * .paths
     *   PathSelectors 路径扫描接口
     *      ant 配置以xxx 开头的路径
     * @return
     */
    @Bean
    public Docket docket( ){

        return  new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("dcloud")
                .select()
                //这里采用包含注解的方式来确定要显示的接口(建议使用这种)
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                //.apis(RequestHandlerSelectors.basePackage("com.dcloud.**.controller"))
                .paths(PathSelectors.any())
                .build();//构建者模式
    }
    /**
     * 配置Swagger信息 apiinfo
     * @return
     */
    private ApiInfo apiInfo(){
        //配置作者信息
        Contact DEFAULT_CONTACT = new Contact("dcloud", "", "ittcsd@163.com");
        return  new ApiInfo(
                "dcloud 的Swagger API文档",
                "dcloud项目接口文档",
                "v1.0",
                "",
                DEFAULT_CONTACT,
                "",
                "",
                new ArrayList());
    }

}
