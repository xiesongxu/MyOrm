package com.xie.Annotation;

import java.lang.annotation.*;

/**
 * 这个框架的配置的注解
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {


    /**
     *
     * @return 返回驱动类
     */
    String driver() default "";

    /**
     *
     * @return 返回数据库连接地址
     */
    String url() default "";

    /**
     *
     * @return 返回登录数据库的用户名
     */
    String user() default "";

    /**
     *
     * @return 返回登录数据库的密码
     */
    String password() default "";

    /**
     *
     * @return 返回框架的扫描路径
     */
    String doScan() default "";


}
