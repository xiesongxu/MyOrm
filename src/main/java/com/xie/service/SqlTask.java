package com.xie.service;

/**
 * 用于记录任务
 */
public class SqlTask implements Comparable {

    // 目标方法
    private TargetMethod targetMethod;

    // sql语句类型
    private String sqlType;

    public SqlTask(TargetMethod targetMethod, String sqlType) {
        this.targetMethod = targetMethod;
        this.sqlType = sqlType;
    }

    public TargetMethod getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(TargetMethod targetMethod) {
        this.targetMethod = targetMethod;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public String toString() {
        return "SqlTask{" +
                "targetMethod=" + targetMethod +
                ", sqlType='" + sqlType + '\'' +
                '}';
    }
}
