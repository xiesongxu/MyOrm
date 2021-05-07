package com.xie.execute;

import com.xie.service.SqlTask;
import com.xie.service.TargetMethod;
import com.xie.service.TargetObject;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 高并发处理数据库连接
 */
public class MultiThreadExecute {

    // 单线程执行器集合
    private static final List<SingleExecute> singles = new ArrayList<>();

    // 线程池
//    private static final ExecutorService executor = Executors.newFixedThreadPool(20);

    // 取对应单线程执行器的下标
    private static int index = 0;

    private static Logger logger = null;

    /**
     * 执行
     * @param targetMethod
     * @param sqlType
     * @return
     */
    public Object execute(TargetMethod targetMethod, String sqlType) {
        if (logger == null) {
            logger = targetMethod.getSqlConfig().getLogger();
        }
        TargetObject targetObject = doExecute(targetMethod, sqlType);
        return targetObject.getTargetObject();
    }

    /**
     * 多线程执行，遍历单线程执行对应的任务
     * @param targetMethod
     * @param sqlType
     * @return
     */
    public TargetObject doExecute(TargetMethod targetMethod, String sqlType) {
        synchronized (singles) {
            int size = singles.size();
            if (size < 20) {
                SingleExecute singleExecute = new SingleExecute();
                // 添加单线程执行器
                singles.add(singleExecute);
                SqlTask sqlTask = new SqlTask(targetMethod, sqlType);
                // 添加任务
                singleExecute.addTask(sqlTask);
                Thread thread = new Thread(singleExecute);
                thread.setDaemon(true);
                if (logger.isInfoEnabled()) {
                    logger.info("添加了一个新的sql任务：" + sqlTask + "  线程为：" + thread.getName());
                }
                thread.start();
                return new TargetObject(sqlTask, singleExecute);
            } else {
                // 找到对应的单线程执行器
                SingleExecute singleExecute = singles.get(index % size);
                // 创建任务
                SqlTask sqlTask = new SqlTask(targetMethod, sqlType);
                if (logger.isInfoEnabled()) {
                    logger.info("添加了一个新的sql任务：" + sqlTask + "  线程为：" + singleExecute.getSingleThread().getName());
                }
                singleExecute.addTask(sqlTask);
                singleExecute.unLock();
                index++;
                return new TargetObject(sqlTask, singleExecute);
            }
        }
    }

}
