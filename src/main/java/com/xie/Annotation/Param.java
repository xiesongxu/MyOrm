package com.xie.Annotation;

import java.lang.annotation.*;

/**
 * 标识参数名
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    //参数名
    String value() default "";

}
