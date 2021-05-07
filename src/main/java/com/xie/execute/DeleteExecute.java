package com.xie.execute;

import com.xie.Annotation.Delete;
import com.xie.exception.SqlException;
import com.xie.service.TargetMethod;

/**
 * delete语句的执行器
 */
public class DeleteExecute extends CacheExecute{

    public Object doDelete(TargetMethod targetMethod, Delete delete) {
        String value = delete.value();
        String sql = null;
        try {
            sql = completeSqlStatement(value, targetMethod);
        } catch (SqlException e) {
            e.printStackTrace();
        }
        return doCache(targetMethod, sql,"delete");
    }
}
