package com.xie.service;

import com.xie.config.SqlConfig;

import java.lang.reflect.Method;

/**
 * 记录调用的目标方法属性
 */
public class TargetMethod {

    //目标接口类类型
    private Class targetClass;

    //目标方法
    private Method targetMethod;

    //方法参数
    private Object[] methodArgs;

    //框架配置
    private SqlConfig sqlConfig;

    //执行的sql语句
    private String sqlStatement;

    public TargetMethod(Class targetClass, Method targetMethod, Object[] methodArgs, SqlConfig sqlConfig) {
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
        this.methodArgs = methodArgs;
        this.sqlConfig = sqlConfig;
    }

    public String getSqlStatement() {
        return sqlStatement;
    }

    public void setSqlStatement(String sqlStatement) {
        this.sqlStatement = sqlStatement;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(Method targetMethod) {
        this.targetMethod = targetMethod;
    }

    public Object[] getMethodArgs() {
        return methodArgs;
    }

    public void setMethodArgs(Object[] methodArgs) {
        this.methodArgs = methodArgs;
    }

    public SqlConfig getSqlConfig() {
        return sqlConfig;
    }

    public void setSqlConfig(SqlConfig sqlConfig) {
        this.sqlConfig = sqlConfig;
    }
}
