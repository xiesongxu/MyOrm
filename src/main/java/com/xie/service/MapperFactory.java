package com.xie.service;

import com.xie.handler.MapperHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于处理和加工MapperHandler
 */
public class MapperFactory {

    private List<MapperHandler> lists = new ArrayList<>();

    public MapperFactory(List<MapperHandler> lists) {
        this.lists = lists;
    }

    /**
     * 获取对应的代理后的对象
     * @param target
     * @return
     */
    public Object getTarget(Class<?> target) {
        MapperHandler handler = null;
        for (MapperHandler mapper : lists) {
            if (mapper.getType().equals(target)) {
                handler = mapper;
                break;
            }
        }
        Object proxyObject = handler.getProxyObject();
        return proxyObject;
    }

}
