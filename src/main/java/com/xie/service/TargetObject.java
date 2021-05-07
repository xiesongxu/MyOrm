package com.xie.service;

import com.xie.execute.SingleExecute;

/**
 * 用于保存执行的任务 与 单线程执行器
 */
public class TargetObject {

    // 执行的任务
    private SqlTask sqlTask;

    // 单线程执行器
    private SingleExecute singleExecute;

    public TargetObject(SqlTask sqlTask, SingleExecute singleExecute) {
        this.sqlTask = sqlTask;
        this.singleExecute = singleExecute;
    }

    /**
     * 获取目标对象
     * @return
     */
    public Object getTargetObject() {
        return singleExecute.getTarget(sqlTask);
    }

    public SqlTask getSqlTask() {
        return sqlTask;
    }

    public void setSqlTask(SqlTask sqlTask) {
        this.sqlTask = sqlTask;
    }

    public SingleExecute getSingleExecute() {
        return singleExecute;
    }

    public void setSingleExecute(SingleExecute singleExecute) {
        this.singleExecute = singleExecute;
    }
}
