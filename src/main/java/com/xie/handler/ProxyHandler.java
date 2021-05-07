package com.xie.handler;

import com.xie.config.SqlConfig;
import com.xie.execute.MethodExecute;
import com.xie.service.TargetMethod;
import com.xie.tool.MyProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 给映射接口进行代理
 */
public class ProxyHandler {

    private Class target;

    private SqlConfig sqlConfig;

    public ProxyHandler(Class target) {
        this.target = target;
    }

    /**
     * 开始代理
     * @return
     */
    public Object doProxy() {
        return  Proxy.newProxyInstance(target.getClassLoader(),new Class[]{target},new MyInvocationHandler());
    }

    public SqlConfig getSqlConfig() {
        return sqlConfig;
    }

    public void setSqlConfig(SqlConfig sqlConfig) {
        this.sqlConfig = sqlConfig;
    }

    private class MyInvocationHandler implements InvocationHandler {

        /**
         * 代理逻辑
         * @param proxy
         * @param method
         * @param args
         * @return
         * @throws Throwable
         */
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            TargetMethod targetMethod = new TargetMethod(target, method, args, sqlConfig);
            MethodExecute methodExecute = new MethodExecute(targetMethod);
            return methodExecute.executeMethod();
        }

    }
}
