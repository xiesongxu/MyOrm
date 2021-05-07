package com.xie.test;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.xie.Annotation.Config;
import com.xie.dao.Dao1;
import com.xie.dao.User;
import com.xie.exception.NConfigException;
import com.xie.exception.NDataConfigException;
import com.xie.exception.NPathException;
import com.xie.handler.MapperHandler;
import com.xie.service.MapperFactory;
import com.xie.tool.Bootstrap;
import com.xie.tool.Parse;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.AppenderAttachableImpl;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.List;

@Config(driver = "com.mysql.jdbc.Driver",
        url = "jdbc:mysql://localhost:3306/xie",
        user = "root",
        password = "12345",
        doScan = "com.xie.dao")
public class Test1 {

    public void get(int test) {

    }

    public static void main(String[] args) throws NConfigException, NDataConfigException, NPathException {
        MapperFactory mapperFactory = Bootstrap.init(Test1.class);
        Dao1 target = (Dao1) mapperFactory.getTarget(Dao1.class);
//        System.out.println(target.select());
//        List<User> select = target.selectAll();
//        User user = select.get(0);
//        System.out.println(user);
//        User user = target.selectOne(1);
//        System.out.println(user);
//        User user1 = target.selectOne(2);
//        System.out.println(user1);
//        User user12 = target.selectOne(2);
//        System.out.println(user12);
//        User user123 = target.selectOne(1);
//        System.out.println(user123);

        for (int i = 0; i < 200; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    User user123 = target.selectOne(1);
                    System.out.println(user123);
                }
            }).start();
        }
        //        Logger rootLogger = Logger.getRootLogger();
//        rootLogger.info("[%-5p] %d{yyyy-MM-dd HH:mm:ss,SSS} method:%l%n%m%n"+"xiesongxu");
//        rootLogger.addAppender(new AppenderAttachableImpl());
//        try {
//            Method get = Test1.class.getDeclaredMethod("get", int.class);
//            Parameter parameter = get.getParameters()[0];
//            System.out.println(parameter.getName());
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }

//        User select = target.select();
//        System.out.println(select);
//        try {
//            //C:\Users\18343\Desktop\xin\MyOrm\target\classes\com\xie\dao\Dao1.class
//            Class<?> aClass = Class.forName("com.xie.dao.Dao1");
//            System.out.println(aClass);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }

//        URL resource = Test1.class.getClassLoader().getResource("");
//        System.out.println(resource.getPath());

//        System.out.println(Test1.class.);
//        System.out.println("\'" + "xieongsxu");
//        System.out.println(Object.class.isAssignableFrom(Test1.class));
//        try {
//            Method get = Test1.class.getDeclaredMethod("get", int.class);
//            System.out.println(get);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        }
    }
}
