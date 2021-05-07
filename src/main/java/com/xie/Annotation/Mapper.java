package com.xie.Annotation;

import java.lang.annotation.*;

/**
 * 加在接口上，表示当前接口为映射接口
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapper {
}
