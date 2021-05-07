package com.xie.tool;

import com.xie.Annotation.Mapper;
import com.xie.exception.NFileException;
import com.xie.handler.MapperHandler;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析映射接口
 */
public class ParseMapper {

    //存放映射处理器
    private List<MapperHandler> mappers = new ArrayList<MapperHandler>();

    /**
     * 开始解析映射类
     */
    public List<MapperHandler> doParseMapper(String path) {
        String resource = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String s = path.replace("." ,"/");
        try {
            parsePath(resource + s);
        } catch (NFileException e) {
            e.printStackTrace();
        }
        return mappers;
    }

    /**
     * 解析扫描路径下的文件
     * @param s
     * @throws NFileException
     */
    public void parsePath(String s) throws NFileException {
        File file = new File(s);
        if ( !file.exists() ) {
            throw new NFileException("");
        }
        checkFile(file);
    }

    /**
     * 检测是否是文件或文件夹
     * @param file
     */
    public void checkFile(File file) {
        if ( file.isDirectory() ) {
            File[] files = file.listFiles();
            for ( File f : files) {
                checkFile(f);
            }
        } else {
            String fileName = file.getAbsolutePath();
            URL resource = ParseMapper.class.getClassLoader().getResource("");
            String path = resource.getPath();
            int length = path.length();
            String substring = fileName.substring(length - 1, fileName.length() - 6);
            String replace = substring.replace("\\", ".");
            checkMapper(replace);
        }
    }

    /**
     * 检测当前类类型是否是映射接口
     * @param s
     */
    public void checkMapper(String s) {
        Class<?> aClass = null;
        try {
            aClass = Class.forName(s);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        //检测是否是接口
        if (!aClass.isInterface()) {
            return;
        }
        //是否有@Mapper注解
        if (aClass.getAnnotation(Mapper.class) != null) {
            mappers.add(new MapperHandler(aClass));
        }
    }



}
