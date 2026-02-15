package com.example.redis.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解
 * 用在接口方法上，标记该接口需要限流
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * 限流时间窗口，单位：秒
     */
    int time() default 60;

    /**
     * 允许的最大请求次数
     */
    int count() default 100;

    /**
     * 限流提示消息
     */
    String msg() default "请求过于频繁，请稍后再试";
}
