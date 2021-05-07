package com.xie.handler;

import com.xie.config.SqlConfig;

/**
 * 处理映射接口
 */
public class MapperHandler {

    //代理的目标接口
    private Class target;

    //代理处理类
    private ProxyHandler proxyHandler;

    //框架配置类
    private SqlConfig sqlConfig;

    public MapperHandler(Class target) {
        this.target = target;
        this.proxyHandler = new ProxyHandler(target);
    }

    /**
     * 获取目标接口的类类型
     * @return
     */
    public Class getType() {
        return target;
    }

    /**
     * 获取接口的简单名字
     * @return
     */
    public String getSimpleNameOfType() {
        return target.getName();
    }

    /**
     * 获取代理后的对象
     * @return
     */
    public Object getProxyObject() {
        return proxyHandler.doProxy();
    }

    public SqlConfig getSqlConfig() {
        return sqlConfig;
    }

    public void setSqlConfig(SqlConfig sqlConfig) {
        this.sqlConfig = sqlConfig;
        proxyHandler.setSqlConfig(sqlConfig);
    }
}
