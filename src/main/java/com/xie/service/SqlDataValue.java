package com.xie.service;

import java.util.ArrayList;
import java.util.List;

/**
 * 代表架构缓存的value
 */
public class SqlDataValue<T> {

    private Class<T> type;

    private List data;

    public SqlDataValue(Class s, List list) {
        this.type = s;
        this.data = list;
    }

    public int dataLength() {
        return data.size();
    }

    public List getData() {
        return data;
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

    public void setData(List data) {
        this.data = data;
    }
}
