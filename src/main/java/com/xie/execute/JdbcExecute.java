package com.xie.execute;

import com.xie.config.SqlConfig;
import com.xie.service.SqlTask;
import com.xie.service.TargetMethod;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 执行sql语句
 */
public class JdbcExecute {

    private static Logger logger = null;

    /**
     * 执行sql语句，并返回目标值
     * @param sqlTask
     * @return
     */
    public Object executeSql(SqlTask sqlTask) {
        Object target = null;
        String sqlType = sqlTask.getSqlType();
        TargetMethod targetMethod = sqlTask.getTargetMethod();
        if (logger == null) {
            logger = targetMethod.getSqlConfig().getLogger();
        }
        String sqlStatement = targetMethod.getSqlStatement();
        if (logger.isInfoEnabled()) {
            logger.info("数据库执行的sql语句为：" + sqlStatement);
        }
        SqlConfig sqlConfig = targetMethod.getSqlConfig();
        Connection connection = sqlConfig.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            if (sqlType.equals("select")) {
                ResultSet resultSet = preparedStatement.executeQuery();
                List<HashMap<String, Object>> hashMaps = parseResultSet(resultSet);
                target = parseTarget(hashMaps, targetMethod.getTargetMethod());
            } else {
                target = preparedStatement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return target;
    }


    /**
     * 解析结果集
     * @param resultSet
     * @return
     */
    public List<HashMap<String, Object>> parseResultSet(ResultSet resultSet) {
        HashMap<String,String> nameAndType = new HashMap();
        List<HashMap<String,Object>> target = new ArrayList<>();
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                nameAndType.put(metaData.getColumnName(i), metaData.getColumnTypeName(i));
            }
            Set set = nameAndType.keySet();
            while (resultSet.next()) {
                HashMap<String,Object> nameAndTarget = new HashMap<>();
                for (Object o : set) {
                    String name = (String) o;
                    String s = nameAndType.get(o);
                    if (s.equals("VARBINARY")) {
                        byte[] bytes = resultSet.getBytes(name);
                        nameAndTarget.put(name, bytes);
                    } else if (s.equals("FLOAT")) {
                        float aFloat = resultSet.getFloat(name);
                        nameAndTarget.put(name, aFloat);
                    } else if (s.equals("DATE")) {
                        Date date = resultSet.getDate(name);
                        nameAndTarget.put(name, date);
                    } else if (s.equals("TINYINT")) {
                        int anInt = resultSet.getInt(name);
                        nameAndTarget.put(name, anInt);
                    } else if (s.equals("BIT")) {
                        byte aByte = resultSet.getByte(name);
                        nameAndTarget.put(name, aByte);
                    } else if (s.equals("BIGINT")) {
                        int anInt = resultSet.getInt(name);
                        nameAndTarget.put(name, anInt);
                    } else if (s.equals("LONGVARBINARY")) {
                        byte[] bytes = resultSet.getBytes(name);
                        nameAndTarget.put(name, bytes);
                    } else if (s.equals("BINARY")) {
                        byte aByte = resultSet.getByte(name);
                        nameAndTarget.put(name, aByte);
                    } else if (s.equals("LONGVARCHAR")) {
                        String string = resultSet.getString(name);
                        nameAndTarget.put(name, string);
                    } else if (s.equals("NULL")) {
                        nameAndTarget.put(name, null);
                    } else if (s.equals("CHAR")) {
                        String string = resultSet.getString(name);
                        nameAndTarget.put(name, string);
                    } else if (s.equals("NUMERIC")) {
                        int anInt = resultSet.getInt(name);
                        nameAndTarget.put(name, anInt);
                    } else if (s.equals("DECIMAL")) {
                        BigDecimal bigDecimal = resultSet.getBigDecimal(name);
                        nameAndTarget.put(name, bigDecimal);
                    } else if (s.equals("INTEGER")) {
                        int anInt = resultSet.getInt(name);
                        nameAndTarget.put(name, anInt);
                    } else if (s.equals("SMALLINT")) {
                        int anInt = resultSet.getInt(name);
                        nameAndTarget.put(name, anInt);
                    } else if (s.equals("REAL")) {
    //                    resultSet.g
                    } else if (s.equals("DOUBLE")) {
                        double aDouble = resultSet.getDouble(name);
                        nameAndTarget.put(name, aDouble);
                    } else if (s.equals("VARCHAR")) {
                        String string = resultSet.getString(name);
                        nameAndTarget.put(name, string);
                    } else if (s.equals("TIME")) {
                        Time time = resultSet.getTime(name);
                        nameAndTarget.put(name, time);
                    } else if (s.equals("TIMESTAMP")) {
                        Timestamp timestamp = resultSet.getTimestamp(name);
                        nameAndTarget.put(name, timestamp);
                    } else {
                        nameAndTarget.put(name, null);
                    }
                }
                target.add(nameAndTarget);
            }
            return target;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析数据库返回的值 并 获取目标对象
     * @param hashMaps
     * @param method
     * @return
     */
    public Object parseTarget(List<HashMap<String, Object>> hashMaps, Method method) {
        Class returnType = method.getReturnType();
        if (List.class.isAssignableFrom(returnType)) {
            Type genericReturnType = method.getGenericReturnType();
            int size = hashMaps.size();
            List<Object> target = new ArrayList();
            Class<?> actualType = null;
            Field[] declaredFields = null;
            if (genericReturnType instanceof ParameterizedType) {
                try {
                    Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
                    //因为list泛型只有一个值 所以直接取0下标
                    String typeName = actualTypeArguments[0].getTypeName();
                    //真实返回值类型 Class对象
                    actualType = Class.forName(typeName);
                    declaredFields = actualType.getDeclaredFields();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            // 遍历数据库返回的值
            for (int i = 0; i < size; i++) {
                HashMap<String, Object> objectHashMap = hashMaps.get(i);
                Object o = null;
                try {
                    o = actualType.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                // 遍历对象的参数
                for (Field field : declaredFields) {
                    field.setAccessible(true);
                    String name = field.getName();
                    Object value = objectHashMap.get(name);
                    try {
                        field.set(o, value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                target.add(o);
            }
            return target;
        } else {
            Field[] declaredFields = returnType.getDeclaredFields();
            HashMap<String, Object> objectHashMap = hashMaps.get(0);
            Object o = null;
            try {
                o = returnType.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            for (Field field : declaredFields) {
                field.setAccessible(true);
                String name = field.getName();
                Object value = objectHashMap.get(name);
                try {
                    field.set(o, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return o;
        }
    }

}
