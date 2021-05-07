package com.xie.Annotation;

import java.lang.annotation.*;

/**
 * 填写删除sql语句
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Delete {

    String value() default "";

}
