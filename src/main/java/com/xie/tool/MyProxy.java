package com.xie.tool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MyProxy<T> {

    private Class<T> target;

    public MyProxy(Class<T> target) {
        this.target = target;
    }

    public T doProxy() {
        return (T) Proxy.newProxyInstance(target.getClassLoader(),new Class[]{target},new MyInvocationHandler());
    }


    private class MyInvocationHandler implements InvocationHandler {

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return null;
        }
    }

}
