package com.xie.Annotation;


import java.lang.annotation.*;

/**
 * 填写修改sql语句
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Update {


    String value() default "";
}
