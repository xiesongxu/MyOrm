package com.xie.execute;

import com.xie.Annotation.Update;
import com.xie.exception.SqlException;
import com.xie.service.TargetMethod;

/**
 * update语句的执行器
 */
public class UpdateExecute extends CacheExecute {


    public Object doUpdate(TargetMethod targetMethod, Update update) {
        String value = update.value();
        String sql = null;
        try {
            sql = completeSqlStatement(value, targetMethod);
        } catch (SqlException e) {
            e.printStackTrace();
        }
        return doCache(targetMethod, sql,"update");
    }
}
