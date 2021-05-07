package com.xie.config;

import com.xie.handler.MapperHandler;
import com.xie.tool.ParseMapper;
import lombok.Data;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 * 框架的配置类，用于配置整个框架
 */
public class SqlConfig {

    private String driver;//驱动路径
    private String url;//数据库地址
    private String user;//访问数据库的用户名
    private String password;//用户密码

    //框架的扫描的路径
    private String doScan;

    private ParseMapper parseMapper = new ParseMapper();

    private Logger logger = Logger.getRootLogger();

    /**
     * 获取logger
     * @return
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * 框架配置的初始化
     */
    public List<MapperHandler> init() {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        List<MapperHandler> mapperHandlerList = parseMapper.doParseMapper(doScan);
        for (MapperHandler mapperHandler : mapperHandlerList) {
            mapperHandler.setSqlConfig(this);
        }
        return  mapperHandlerList;
    }

    /**
     * 获取数据库连接（创建一个新的数据库连接返回）
     * @return
     */
    public Connection getConnection() {
        synchronized (this) {
            try {
                return DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * 检测连接数据库的四大基本配置是否有缺失的配置
     * @return true：有缺陷
     */
    public boolean checkDataConfig() {
        if ( driver == null || driver.equals("") ||
             url == null || url.equals("") ||
             user == null || user.equals("") ||
             password == null || password.equals("")) {
            return true;
        }
        return false;
    }

    public String getDoScan() {
        return doScan;
    }

    public void setDoScan(String doScan) {
        this.doScan = doScan;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
