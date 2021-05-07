package com.xie.tool;

import com.xie.config.SqlConfig;
import com.xie.exception.NConfigException;
import com.xie.exception.NDataConfigException;
import com.xie.exception.NPathException;
import com.xie.handler.MapperHandler;
import com.xie.service.MapperFactory;
import sun.java2d.pipe.SpanClipRenderer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * 引导类
 */
public class Bootstrap {

    //配置类
    private static SqlConfig sqlConfig;

    private static Properties properties = new Properties();

    private static final String configFile = "configuration.properties";

    private static final String url = "url";

    private static final String driver = "driver";

    private static final String user = "user";

    private static final String password = "password";

    /**
     * 初始化整个架构
     * @param target
     */
    public static MapperFactory init(Class target) {
        try {
            sqlConfig = Parse.doParse(target);
        } catch (NConfigException e) {
            e.printStackTrace();
        } catch (NPathException e) {
            e.printStackTrace();
        }
        //检测配置是否完整
        checkSqlConfig(sqlConfig);
        List<MapperHandler> mapperHandlers = sqlConfig.init();
        MapperFactory mapperFactory = new MapperFactory(mapperHandlers);
        return mapperFactory;
    }

    /**
     * 检测sqlConfig对象中连接数据库的四大基本配置是否有缺失
     * @param sqlConfig
     */
    public static void checkSqlConfig(SqlConfig sqlConfig) {
        boolean b = sqlConfig.checkDataConfig();
        if ( b ) {
            try {
                parseProperties();
            } catch (NDataConfigException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从配置文件中读取配置
     */
    public static void parseProperties() throws NDataConfigException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(configFile);
        try {
            properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String driver1 = (String) properties.get(driver);
        if ( driver1 == null || driver.equals("") ) {
            throw new NDataConfigException("没有配置driver");
        }
        sqlConfig.setDriver(driver1);
        String url1 = (String) properties.get(url);
        if ( url1 == null || url1.equals("") ) {
            throw new NDataConfigException("没有配置url");
        }
        sqlConfig.setUrl(url1);
        String user1 = (String) properties.get(user);
        if ( user1 == null || user1.equals("") ) {
            throw new NDataConfigException("没有配置user");
        }
        sqlConfig.setUser(user1);
        String password1 = (String) properties.get(password);
        if ( password1 == null || password1.equals("") ) {
            throw new NDataConfigException("password");
        }
        sqlConfig.setPassword(password1);
    }


}
