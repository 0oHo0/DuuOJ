package com.duu.ojcommon.annotation;

import java.lang.annotation.*;

/**
 * 接口访问频率注解，默认一分钟只能访问5次  
 */  
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestLimit {  
  
    // 限制时间 单位：秒(默认值：一分钟）  
    long period() default 60;  
  
    // 允许请求的次数(默认值：5次）  
    long count() default 5;  
  
}