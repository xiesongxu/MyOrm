package com.xie.Annotation;

import java.lang.annotation.*;

/**
 * 填写查找sql语句
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Select {

    String value() default "";

}
