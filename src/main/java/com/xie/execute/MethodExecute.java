package com.xie.execute;

import com.xie.Annotation.Delete;
import com.xie.Annotation.Insert;
import com.xie.Annotation.Select;
import com.xie.Annotation.Update;
import com.xie.service.TargetMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 方法的执行器，将执行目标方法所设定的逻辑
 */
public class MethodExecute {

    //包装的目标方法
    private TargetMethod targetMethod;

    //select语句的执行器
    private static SelectExecute selectExecute = new SelectExecute();

    private static InsertExecute insertExecute = new InsertExecute();

    private static UpdateExecute updateExecute = new UpdateExecute();

    private static DeleteExecute deleteExecute = new DeleteExecute();

    public MethodExecute(TargetMethod targetMethod) {
        this.targetMethod = targetMethod;
    }

    /**
     * 执行方法对应的逻辑
     * @return
     */
    public Object executeMethod() {
        Method method = this.targetMethod.getTargetMethod();

        //处理select类型的sql语句
        Select select = method.getAnnotation(Select.class);
        if (select != null) {
            return selectExecute.doSelect(targetMethod, select);
        }

        //处理insert类型的sql语句
        Insert insert = method.getAnnotation(Insert.class);
        if (insert != null) {
            return insertExecute.doInsert(targetMethod, insert);
        }

        //处理update类型的sql语句
        Update update = method.getAnnotation(Update.class);
        if (update != null) {
            return updateExecute.doUpdate(targetMethod, update);
        }

        //处理delete类型的sql语句
        Delete delete = method.getAnnotation(Delete.class);
        if (delete != null) {
            return deleteExecute.doDelete(targetMethod, delete);
        }
        return null;
    }

}
