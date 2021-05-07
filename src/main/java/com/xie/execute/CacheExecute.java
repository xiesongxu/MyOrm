package com.xie.execute;

import com.xie.Annotation.Param;
import com.xie.exception.SqlException;
import com.xie.service.SqlDataKey;
import com.xie.service.SqlDataValue;
import com.xie.service.TargetMethod;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于缓存的处理
 */
public class CacheExecute {

    //整个架构的缓存，缓存sql数据，
    private static final ConcurrentHashMap<String, SqlDataValue> sqlCache = new ConcurrentHashMap();

    //高并发处理器
    private static final MultiThreadExecute multiThreadExecute = new MultiThreadExecute();

    private static Logger logger = null;

    /**
     * 判断缓存里是否有对应的数据，如果有就从缓存里获取
     * @param targetMethod
     * @param sql
     * @param sqlType
     * @return
     */
    public Object doCache(TargetMethod targetMethod, String sql, String sqlType) {
        if (logger == null) {
            logger = targetMethod.getSqlConfig().getLogger();
        }
        targetMethod.setSqlStatement(sql);
        if (sqlType.equals("insert")) {
            return multiThreadExecute.execute(targetMethod, sqlType);
        }
        String chartName = getChartName(sql, sqlType);
        SqlDataValue sqlDataValue = sqlCache.get(chartName);
        if (sqlDataValue == null || sqlDataValue.dataLength() == 0) {
            if (sqlType.equals("select")) {
                Object executeResult = multiThreadExecute.execute(targetMethod, sqlType);
                if (executeResult == null) {
                    return null;
                }
                Class<?> aClass = executeResult.getClass();
                if (aClass.isAssignableFrom(List.class)) {    //todo
                    List list = (List) executeResult;
                    if (list.isEmpty()) {
                        sqlCache.put(chartName, null);
                        return null;
                    } else {
                        Object targetObject = list.get(0);
                        Class<?> targetObjectClass = targetObject.getClass();
                        sqlCache.put(chartName, new SqlDataValue(targetObjectClass, list));
                        return list;
                    }
                } else {
                    ArrayList<Object> arrayList = new ArrayList<>();
                    arrayList.add(executeResult);
                    sqlCache.put(chartName, new SqlDataValue(aClass, arrayList));
                    return executeResult;
                }
            }
            return multiThreadExecute.execute(targetMethod, sqlType);
        }
        if (sqlType.equals("delete")) {
            //从缓存里获取对应集合，然后删除
            List<Object> objects = findDTargetObjectsFromCache(sqlDataValue, sql);
            if (objects != null && objects.size() > 0) {
                List data = sqlDataValue.getData();
                data.removeAll(objects);
            }
            return multiThreadExecute.execute(targetMethod, sqlType);
        }
        if (sqlType.equals("update")) {
            sqlCache.remove(chartName);
            return multiThreadExecute.execute(targetMethod, sqlType);
        }
        if (sqlType.equals("select")) {
            //如果是> 或 <条件语句，就直接从数据库里查询
            List<Object> objects = findSTargetObjectsFromCache(sqlDataValue, sql, targetMethod);
            if (objects.size() == 0) {
                Object execute = multiThreadExecute.execute(targetMethod, sqlType);
                if (List.class.isAssignableFrom(execute.getClass())) {
                    sqlDataValue.getData().addAll((List) execute);
                } else {
                    sqlDataValue.getData().add(execute);
                }
                return execute;
            }
            Method targetMethod1 = targetMethod.getTargetMethod();
            Class<?> returnType = targetMethod1.getReturnType();
            if (List.class.isAssignableFrom(returnType)) {
                return objects;
            } else {
                return objects.get(0);
            }
        }
        return null;
    }

    /**
     * 取出参数名，与 参数值，与 取值符号的对应关系
     * @param sqlDataValue
     * @param sql
     * @param targetMethod
     * @return
     */
    public List<Object> findSTargetObjectsFromCache(SqlDataValue sqlDataValue, String sql, TargetMethod targetMethod) {
        // todo 如果这个集合经过处理后，还没有元素存在，就可能是全查询。
        HashMap<String,String> conditionMap = new HashMap();
        HashMap<String,String> opinionMap = new HashMap<>();
        String[] split = sql.split(" ");
        for (String s : split) {
            boolean less = s.contains("<");
            boolean greater = s.contains(">");
            boolean equal = s.contains("=");
            if (less) {
                int indexOf = s.indexOf("<");
               if (equal) {
                   conditionMap.put(s.substring(0, indexOf), s.substring(indexOf + 2));
                   opinionMap.put(s.substring(0, indexOf), "<=");
               } else {
                   conditionMap.put(s.substring(0, indexOf), s.substring(indexOf + 1));
                   opinionMap.put(s.substring(0, indexOf), "<");
               }
            } else if (greater) {
                int indexOf = s.indexOf(">");
                if (equal) {
                    conditionMap.put(s.substring(0, indexOf), s.substring(indexOf + 2));
                    opinionMap.put(s.substring(0, indexOf), ">=");
                } else {
                    conditionMap.put(s.substring(0, indexOf), s.substring(indexOf + 1));
                    opinionMap.put(s.substring(0, indexOf), ">");
                }
            } else if (equal) {
                int indexOf = s.indexOf("=");
                conditionMap.put(s.substring(0, indexOf), s.substring(indexOf + 1));
                opinionMap.put(s.substring(0, indexOf), "=");
            } else {
                continue;
            }
        }
        if (conditionMap.size() == 0 || opinionMap.size() == 0) {
            Object select = multiThreadExecute.execute(targetMethod, "select");
            sqlDataValue.setData((List) select);
            return (List<Object>) select;
        }
        List<Object> objects = checkSFromCache(sqlDataValue, conditionMap, opinionMap, targetMethod);
        return objects;
    }

    /**
     * 从缓存里获取数据，如果是>= 或 <=条件，就删除缓存里对应的数据，然后从数据库里查询数据然后返回
     * =条件就从，缓存里直接获取数据
     * @param sqlDataValue
     * @param condition
     * @param opinion
     * @param targetMethod
     * @return
     */
    public List<Object> checkSFromCache(SqlDataValue sqlDataValue, HashMap condition, HashMap opinion, TargetMethod targetMethod) {
        List<Object> target = new ArrayList<>();
        //存放符合条件的Field
        HashMap<String,Field> fieldMap = new HashMap<>();
        Class type = sqlDataValue.getType();
        Set set = condition.keySet();
        Field[] fields = type.getDeclaredFields();
        // 存放参数名对应的变量对象
        for (Object s : set) {
            for (Field field : fields) {
                String fieldName = field.getName();
                if (fieldName.equals(s)) {
                    fieldMap.put((String) s, field);
                }
            }
        }
        List data = sqlDataValue.getData();
        for (Object st : set) {
            String value = (String) condition.get((String) st);
            String judge = (String) opinion.get((String) st);
            Field field = fieldMap.get((String) st);
            field.setAccessible(true);
            if (judge.equals("=")) {
                if (field.getType().equals(String.class)) {
                    value = value.substring(1, value.length() - 1);
                    for (Object o : data) {
                        try {
                            Object fieldValue = field.get(o);
                            if (fieldValue.equals(value)) {
                                target.add(o);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    for (Object o : data) {
                        try {
                            Object fieldValue = field.get(o);
//                            Class<?> fieldType = field.getType();
                            // todo 只支持int型数据，比较
                            Integer integer = new Integer(value);
                            if (integer.equals(fieldValue) || integer == fieldValue) {
                                target.add(o);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (judge.contains("<")) {
                if (judge.contains("=")) {
                    for (Object o : data) {
                        try {
                            Integer fieldValue = (Integer) field.get(o);
                            Integer integer = new Integer(value);
                            if (fieldValue <= integer) {
                                target.add(o);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    for (Object o : data) {
                        try {
                            Integer fieldValue = (Integer) field.get(o);
                            Integer integer = new Integer(value);
                            if (fieldValue < integer) {
                                target.add(o);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                data.remove(target);
                Object select = multiThreadExecute.execute(targetMethod, "select");
                target = (List) select;
                data.addAll(target);
            } else if (judge.contains(">")) {
                if (judge.contains("=")) {
                    for (Object o : data) {
                        try {
                            Integer fieldValue = (Integer) field.get(o);
                            Integer integer = new Integer(value);
                            if (fieldValue >= integer) {
                                target.add(o);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    for (Object o : data) {
                        try {
                            Integer fieldValue = (Integer) field.get(o);
                            Integer integer = new Integer(value);
                            if (fieldValue > integer) {
                                target.add(o);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                // todo 移除原来的数据
                data.remove(target);
                Object select = multiThreadExecute.execute(targetMethod, "select");
                target = (List) select;
                // todo 放入新的新的数据
                data.addAll(target);
            } else {
                continue;
            }
        }
        return target;
    }


    /**
     * 取出参数名，与 参数值，与 取值符号的对应关系
     * @param sqlDataValue
     * @param sql
     * @return
     */
    public List<Object> findDTargetObjectsFromCache(SqlDataValue sqlDataValue, String sql) {
        // todo 从缓存里获取（< 或 > 或 =）条件对应的数据
        // 存放参数名 与 参数值
        HashMap<String,String> conditionMap = new HashMap();
        // 存放参数名 与 取值符号
        HashMap<String,String> opinionMap = new HashMap<>();
        String[] split = sql.split(" ");
        for (String s : split) {
            boolean less = s.contains("<");
            boolean greater = s.contains(">");
            boolean equal = s.contains("=");
            if (less) {
                int indexOf = s.indexOf("<");
                if (equal) {
                    conditionMap.put(s.substring(0, indexOf), s.substring(indexOf + 2));
                    opinionMap.put(s.substring(0, indexOf), "<=");
                } else {
                    conditionMap.put(s.substring(0, indexOf), s.substring(indexOf + 1));
                    opinionMap.put(s.substring(0, indexOf), "<");
                }
            } else if (greater) {
                int indexOf = s.indexOf(">");
                if (equal) {
                    conditionMap.put(s.substring(0, indexOf), s.substring(indexOf + 2));
                    opinionMap.put(s.substring(0, indexOf), ">=");
                } else {
                    conditionMap.put(s.substring(0, indexOf), s.substring(indexOf + 1));
                    opinionMap.put(s.substring(0, indexOf), ">");
                }
            } else if (equal) {
                int indexOf = s.indexOf("=");
                conditionMap.put(s.substring(0, indexOf), s.substring(indexOf + 1));
                opinionMap.put(s.substring(0, indexOf), "=");
            } else {
                continue;
            }
        }
        if (conditionMap.size() == 0 || opinionMap.size() == 0) {
            sqlDataValue.setData(null);
            return null;
        }
        List<Object> objects = checkDFromCache(sqlDataValue, conditionMap, opinionMap);
        return objects;
    }

    /**
     * 从缓存里获取数据
     * @param sqlDataValue
     * @param conditionMap
     * @param opinionMap
     * @return
     */
    public List<Object> checkDFromCache(SqlDataValue sqlDataValue, HashMap conditionMap, HashMap opinionMap) {
        List<Object> target = new ArrayList<>();
        //存放符合条件的Field
        HashMap<String,Field> fieldMap = new HashMap<>();
        Class type = sqlDataValue.getType();
        Set set = conditionMap.keySet();
        Field[] fields = type.getDeclaredFields();
        // 存放参数名对应的变量对象
        for (Object s : set) {
            for (Field field : fields) {
                String fieldName = field.getName();
                if (fieldName.equals(s)) {
                    fieldMap.put((String) s, field);
                }
            }
        }
        // 取出缓存里的数据
        List data = sqlDataValue.getData();
        for (Object st : set) {
            String value = (String) conditionMap.get((String) st);
            String judge = (String) opinionMap.get((String) st);
            Field field = fieldMap.get((String) st);
            if (judge.equals("=")) {
                if (field.getType().equals(String.class)) {
                    value = value.substring(1, value.length() - 1);
                    for (Object o : data) {
                        try {
                            Object fieldValue = field.get(o);
                            if (fieldValue.equals(value)) {
                                target.add(o);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    for (Object o : data) {
                        try {
                            Object fieldValue = field.get(o);
//                            Class<?> fieldType = field.getType();
                            // todo 只支持int型数据，比较
                            Integer integer = new Integer(value);
                            if (integer.equals(fieldValue) || integer == fieldValue) {
                                target.add(o);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (judge.contains("<")) {
                if (judge.contains("=")) {
                    for (Object o : data) {
                        try {
                            Integer fieldValue = (Integer) field.get(o);
                            Integer integer = new Integer(value);
                            if (fieldValue <= integer) {
                                target.add(o);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    for (Object o : data) {
                        try {
                            Integer fieldValue = (Integer) field.get(o);
                            Integer integer = new Integer(value);
                            if (fieldValue < integer) {
                                target.add(o);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (judge.contains(">")) {
                if (judge.contains("=")) {
                    for (Object o : data) {
                        try {
                            Integer fieldValue = (Integer) field.get(o);
                            Integer integer = new Integer(value);
                            if (fieldValue >= integer) {
                                target.add(o);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    for (Object o : data) {
                        try {
                            Integer fieldValue = (Integer) field.get(o);
                            Integer integer = new Integer(value);
                            if (fieldValue > integer) {
                                target.add(o);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                continue;
            }
        }
        return target;
    }

    /**
     * 获取数据库的表名
     * @param sql
     * @param sqlType
     * @return
     */
    public String getChartName(String sql, String sqlType) {
        String[] split = sql.split(" ");
        if (sqlType.equals("delete")) {
            return split[2];
        } else if (sqlType.equals("update")) {
            return split[1];
        } else {
            for (int i = 0; i < split.length; i++) {
                if (split[i].equals("from")) {
                    return split[i+1];
                }
            }
        }
        return null;
    }


    /**
     * 把sql语句进行组合完整，使返回可执行的sql语句
     * @param targetSql
     * @param targetMethod
     * @return
     * @throws SqlException
     */
    public String completeSqlStatement(String targetSql, TargetMethod targetMethod) throws SqlException {
        String[] sqlPart = targetSql.split(" ");
        int length = sqlPart.length;
        for (int i = 0; i < length; i++) {
            String s = sqlPart[i];
            boolean contains = s.contains("$");
            if (contains) {
                int beginIndex = s.indexOf('$');
                int endIndex = s.indexOf('}');
                //截取${}符号里的名字
                String substring = s.substring(beginIndex + 2, endIndex);
                //得出${}符号里的对应值
                String compareSql = compareSql(substring, targetMethod);
                if (compareSql == null) {
                    throw new SqlException("没有对应的参数值");
                }
                if (s.length() == endIndex + 1) {
                    sqlPart[i] = s.substring(0, beginIndex) + compareSql;
                } else if (s.length() > endIndex + 1) {
                    sqlPart[i] = s.substring(0, beginIndex) + compareSql + s.substring(endIndex + 1);
                } else {
                    //......出现错误
                    throw new SqlException("sql语句组合出现错误");
                }
            }
        }
        String toString = "";
        for (int i = 0; i < length; i++) {
            toString += sqlPart[i] + " ";
        }
        return toString.trim();
    }

    /**
     * 把${}符号里的名字换成对应的输入参数值
     * @param subSql
     * @param targetMethod
     * @return
     */
    public String compareSql(String subSql, TargetMethod targetMethod) {
        Method method = targetMethod.getTargetMethod();
        Parameter[] parameters = method.getParameters();
        int length = parameters.length;
        for (int i = 0; i < length; i++) {
            //遍历参数
            Parameter parameter = parameters[i];
            Param annotation = parameter.getAnnotation(Param.class);
            String name = annotation.value();
            boolean contains = subSql.contains(".");
            if (contains) {
                //解析${xie.song}类型
                String[] dataParts = subSql.split(".");
                String data = dataParts[0];
                if (data == name || data.equals(name)) {
                    Class<?> type = parameter.getType();
                    Object argValue = targetMethod.getMethodArgs()[i];
                    String part = dataParts[1];
                    String upperFirst = toUpperFirst(part);
                    try {
                        //获取对象里对应的get方法
                        Method declaredMethod = type.getDeclaredMethod("get" + upperFirst, null);
                        Class<?> returnType = declaredMethod.getReturnType();
                        //调用get方法
                        Object objectMRV = declaredMethod.invoke(argValue, null);
                        if (returnType == String.class || returnType.equals(String.class)) {
                            String sub = (String) objectMRV;
                            sub = "'" + sub + "'";
                            return sub;
                        } else {
                            return "" + objectMRV;
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
//                Method method1 = type.getDeclaredMethod("getName", null);
//                Object invoke = method1.invoke(argValue, null);
            } else {
                //解析普通类型
                if (subSql == name || name.equals(subSql)) {
                    Class<?> type = parameter.getType();
                    Object argValue = targetMethod.getMethodArgs()[i];
                    if (type == String.class || type.equals(String.class)) {
                        String sub = (String) argValue;
                        sub = "'" + sub + "'";
                        return sub;
                    } else {
                        return "" + argValue;
                    }
                }
            }
        }
        return null;
    }


    /**
     * 把字符串首字母变为大写
     * @param s
     * @return
     */
    public String toUpperFirst(String s) {
        String substring = s.substring(0, 1);
        return substring.toUpperCase() + s.substring(1);
    }

}
