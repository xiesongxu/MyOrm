package com.xie.tool;

import com.xie.Annotation.Config;
import com.xie.config.SqlConfig;
import com.xie.exception.NConfigException;
import com.xie.exception.NDataConfigException;
import com.xie.exception.NPathException;

import java.lang.annotation.Annotation;

/**
 * 解析类，解析@Config注解
 * author：谢松序
 */
public class Parse {

    //配置类
    private static SqlConfig sqlConfig = new SqlConfig();

    /**
     * 解析@Config注解的方法，
     * @param target 目标类类型
     * @return 框架配置类
     * @throws NConfigException
     * @throws NPathException
     */
    public static SqlConfig doParse(Class target) throws NConfigException, NPathException {
        Annotation annotation = target.getAnnotation(Config.class);
        if( annotation == null) {
            throw new NConfigException("没有Config注解");
        }
        Config config = (Config) annotation;
        parseData(config);
        parsePath(config);
        return sqlConfig;
    }

    /**
     * 解析连接数据库的四大基本配置
     * @param config @Config注解
     */
    public static void parseData(Config config) {
        sqlConfig.setDriver(config.driver());
        sqlConfig.setUrl(config.url());
        sqlConfig.setUser(config.user());
        sqlConfig.setPassword(config.password());

    }

    /**
     * 解析扫描路径
     * @param config @Config注解
     * @throws NPathException
     */
    public static void parsePath(Config config) throws NPathException {
        if ( config.doScan() == "" || config.doScan().equals("")) {
            throw new NPathException("路径没设置");
        }
        sqlConfig.setDoScan(config.doScan());
    }

}
