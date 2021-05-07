package com.xie.Annotation;

import java.lang.annotation.*;

/**
 * 填写插入sql语句
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Insert {

    String value() default "";
}
