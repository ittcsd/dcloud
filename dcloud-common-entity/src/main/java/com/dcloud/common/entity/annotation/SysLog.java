package com.dcloud.common.entity.annotation;

import com.dcloud.common.entity.log.SysLogTypeEnum;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {

    /**
     * @return 操作类型
     * @see SysLogTypeEnum
     */
    SysLogTypeEnum type() default SysLogTypeEnum.UNDEFINED;

    /**
     * @return 接口描述
     */
    String note() default "";
    /*
    * 所属模块
    * */
    String model() default "";

    /**
     * @return 是否存储请求参数
     */
    boolean saveRequestData() default false;
}