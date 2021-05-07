package com.xie.execute;

import com.xie.Annotation.Insert;
import com.xie.exception.SqlException;
import com.xie.service.TargetMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * insert语句的执行器
 */
public class InsertExecute extends CacheExecute {

    public Object doInsert(TargetMethod targetMethod, Insert insert) {
        String value = insert.value();
        String sql = null;
        try {
            sql = completeSqlStatement(value, targetMethod);
        } catch (SqlException e) {
            e.printStackTrace();
        }
        return doCache(targetMethod, sql,"insert");
    }

//    /**
//     * 把sql语句进行组合完整，使返回可执行的sql语句
//     * @param targetSql
//     * @param targetMethod
//     * @return
//     * @throws SqlException
//     */
//    public String completeSqlStatement(String targetSql, TargetMethod targetMethod) throws SqlException {
//        String[] sqlPart = targetSql.split(" ");
//        int length = sqlPart.length;
//        for (int i = 0; i < length; i++) {
//            String s = sqlPart[i];
//            boolean contains = s.contains("$");
//            if (contains) {
//                int beginIndex = s.indexOf('$');
//                int endIndex = s.indexOf('}');
//                //截取${}符号里的名字
//                String substring = s.substring(beginIndex + 2, endIndex);
//                //得出${}符号里的对应值
//                String compareSql = compareSql(substring, targetMethod);
//                if (compareSql == null) {
//                    throw new SqlException("没有对应的参数值");
//                }
//                if (s.length() == endIndex + 1) {
//                    sqlPart[i] = s.substring(0, beginIndex) + compareSql;
//                } else if (s.length() > endIndex + 1) {
//                    sqlPart[i] = s.substring(0, beginIndex) + compareSql + s.substring(endIndex + 1);
//                } else {
//                    //......出现错误
//                    throw new SqlException("sql语句组合出现错误");
//                }
//            }
//        }
//        String toString = sqlPart.toString();
//        return toString;
//    }
//
//    /**
//     * 把$符号里的名字换成对应的输入参数值
//     * @param subSql
//     * @param targetMethod
//     * @return
//     */
//    public String compareSql(String subSql, TargetMethod targetMethod) {
//        Method method = targetMethod.getTargetMethod();
//        Parameter[] parameters = method.getParameters();
//        int length = parameters.length;
//        for (int i = 0; i < length; i++) {
//            //遍历参数
//            Parameter parameter = parameters[i];
//            String name = parameter.getName();
//            boolean contains = subSql.contains(".");
//            if (contains) {
//                //解析${xie.song}类型
//                String[] dataParts = subSql.split(".");
//                String data = dataParts[0];
//                if (data == name || data.equals(name)) {
//                    Class<?> type = parameter.getType();
//                    Object argValue = targetMethod.getMethodArgs()[i];
//                    String part = dataParts[1];
//                    String upperFirst = toUpperFirst(part);
//                    try {
//                        //获取对象里对应的get方法
//                        Method declaredMethod = type.getDeclaredMethod("get" + upperFirst, null);
//                        Class<?> returnType = declaredMethod.getReturnType();
//                        //调用get方法
//                        Object objectMRV = declaredMethod.invoke(argValue, null);
//                        if (returnType == String.class || returnType.equals(String.class)) {
//                            String sub = (String) objectMRV;
//                            sub = "\"" + sub + "\"";
//                            return sub;
//                        } else {
//                            return "" + objectMRV;
//                        }
//                    } catch (NoSuchMethodException e) {
//                        e.printStackTrace();
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    } catch (InvocationTargetException e) {
//                        e.printStackTrace();
//                    }
//                }
////                Method method1 = type.getDeclaredMethod("getName", null);
////                Object invoke = method1.invoke(argValue, null);
//            } else {
//                //解析普通类型
//                if (subSql == name || name.equals(subSql)) {
//                    Class<?> type = parameter.getType();
//                    Object argValue = targetMethod.getMethodArgs()[i];
//                    if (type == String.class || type.equals(String.class)) {
//                        String sub = (String) argValue;
//                        sub = "\"" + sub + "\"";
//                        return sub;
//                    } else {
//                        return "" + argValue;
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//
//    /**
//     * 把字符串首字母变为大写
//     * @param s
//     * @return
//     */
//    public String toUpperFirst(String s) {
//        String substring = s.substring(0, 1);
//        return substring.toUpperCase() + s.substring(1);
//    }


}
