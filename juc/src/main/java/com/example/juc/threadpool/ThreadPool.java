package com.example.juc.threadpool;

import java.util.Vector;


/**
 * 线程池的实现
 */
public class ThreadPool {
    private static ThreadPool instance = null;
    /**
     * 空闲的线程队列
     */
    private Vector idleThreads;
    /**
     * 已有的线程数
     */
    private int threadCounter;
    /**
     * 是否关闭
     */
    private boolean isShutDown = false;

    private ThreadPool( ) {
        this.idleThreads = new Vector(5);
        threadCounter = 0;
    }

    public int getCreatedThreadsCount( ) {
        return threadCounter;
    }

    /**
     * 获得线程池实例
     *
     * @return
     */
    public synchronized static ThreadPool getInstance( ) {
        if (instance == null)
            instance = new ThreadPool();
        return instance;
    }

    /**
     * 将线程放入线程池
     *
     * @param repoolingThread
     */
    protected synchronized void repool(Worker repoolingThread) {
        if (!isShutDown) {
            idleThreads.add(repoolingThread);
        } else {
            repoolingThread.shutDown();// 关闭线程
        }
    }

    /**
     * 停止线程中所有线程
     */
    public synchronized void shutdown( ) {
        isShutDown = true;
        for (int threadIndex = 0; threadIndex < idleThreads.size(); threadIndex++) {
            Worker idleThread = (Worker) idleThreads.get(threadIndex);
            idleThread.shutDown();
        }
    }

    /**
     * 执行任务
     *
     * @param target
     */
    public synchronized void start(Runnable target) {
        Worker thread = null;
        // 如果有空闲线程,直接使用
        if (idleThreads.size() > 0) {
            int lastIndex = idleThreads.size() - 1;
            thread = (Worker) idleThreads.get(lastIndex);
            idleThreads.remove(lastIndex);
            //立即执行这个任务
            thread.setTarget(target);
        }
        //没有空闲线程则创建线程
        else {
            threadCounter++;
            //创建线程
            thread = new Worker(target, "PThread #" + threadCounter, this);
            //启动这个线程
            thread.start();
        }
    }
}