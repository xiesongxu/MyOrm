package com.xie.execute;

import com.xie.service.SqlTask;
import com.xie.service.TargetMethod;
import com.xie.service.TaskLock;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 单线程执行器，执行sql语句
 */
public class SingleExecute implements Runnable {

    private Thread singleThread;

    // 存放提交的任务
    private PriorityBlockingQueue<SqlTask> queue = new PriorityBlockingQueue();

    // 存放任务 与 任务执行后的结果
    private ConcurrentHashMap<SqlTask,Object> targetMap = new ConcurrentHashMap();

    // 存放任务 与 任务对应的单锁
    private ConcurrentHashMap<SqlTask,TaskLock> lockMap = new ConcurrentHashMap();

    // 单线程执行器的锁
    private ReentrantLock lock = new ReentrantLock();

    // 休息区
    private Condition condition = lock.newCondition();

    // jdbc执行器，执行任务
    private JdbcExecute jdbcExecute = new JdbcExecute();


    public void addTask(SqlTask sqlTask) {
        queue.add(sqlTask);
        targetMap.put(sqlTask, new Object());
        lockMap.put(sqlTask, new TaskLock());
    }

    /**
     * 单线程执行器执行逻辑
     */
    @Override
    public void run() {
        singleThread = Thread.currentThread();
        while (true) {
            int size = queue.size();
            if (size != 0) {
                // 弹出sql任务
                SqlTask sqlTask = queue.poll();
                Object executeSql = jdbcExecute.executeSql(sqlTask);
                // 把执行结果放入map中
                targetMap.put(sqlTask, executeSql);
                TaskLock taskLock = lockMap.get(sqlTask);
                // 给对应线程解锁，以获取结果
                taskLock.unLock();
            } else {
                lock();
            }
        }
    }

    /**
     * 获取sql语句执行的目标结果
     * @param sqlTask
     * @return
     */
    public Object getTarget(SqlTask sqlTask) {
        TaskLock taskLock = lockMap.get(sqlTask);
        taskLock.lock();
        return targetMap.get(sqlTask);
    }

    /**
     * 获取当前执行器里的线程
     * @return
     */
    public Thread getSingleThread() {
        return singleThread;
    }

    /**
     * 给单线程执行器上锁
     */
    public void lock() {
        lock.lock();
        try {
            condition.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 给单线程执行器解锁
     */
    public void unLock() {
        lock.lock();
        try {
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

}
