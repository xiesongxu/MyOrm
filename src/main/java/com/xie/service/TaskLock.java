package com.xie.service;

/**
 * 任务锁，单线程锁
 */
public class TaskLock{

    // 全局变量记录锁的状态
    private int i = 0;

    /**
     * 如果i == 0 把当前线程给锁住，
     */
    public void lock() {
        synchronized (this) {
            while (true) {
                if (i == 0) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
        }
    }

    /**
     * 给当前的锁添加新的状态，唤醒在waitSet区的线程
     */
    public void unLock() {
        synchronized (this) {
            i++;
            notifyAll();
        }
    }

}
