package com.xie.execute;

import com.xie.Annotation.Select;
import com.xie.exception.SqlException;
import com.xie.service.TargetMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * select语句的执行器
 */
public class SelectExecute extends CacheExecute {

    /**
     * 执行目标方法设定的逻辑，返回所设定的值
     * @param targetMethod
     * @param select
     * @return
     */
    public Object doSelect(TargetMethod targetMethod, Select select) {
        String value = select.value();
        String sql = null;
        try {
            sql = completeSqlStatement(value, targetMethod);
        } catch (SqlException e) {
            e.printStackTrace();
        }
        return doCache(targetMethod, sql,"select");
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
//                String substring = s.substring(beginIndex + 2, endIndex);
//                //得出$符号里的对应值
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
//            //判断名字
//            if (name.equals(subSql) || name == subSql) {
//                Class<?> type = parameter.getType();
//                Object argValue = targetMethod.getMethodArgs()[i];
//                if (type == String.class) {
//                    String sub = (String) argValue;
//                    sub = "\"" + sub + "\"";
//                    return sub;
//                } else {
//                    return (String) argValue;
//                }
//            }
//        }
//        return null;
//    }

}
