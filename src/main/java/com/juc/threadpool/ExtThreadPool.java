package com.juc.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池扩展
 *
 * @author Administrator
 */
public class ExtThreadPool {
    public static class MyTask implements Runnable {
        String name;

        MyTask(String name) {
            this.name = name;
        }

        public void run( ) {
            System.out.println("正在执行" + ":Thread ID:" + Thread.currentThread().getId() + ",Task Name=" + name);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        /**
         * 扩展
         */
        ExecutorService es = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>()) {

            //执行前
            protected void beforeExecute(Thread t, Runnable r) {
                System.out.println("准备执行：" + ((MyTask) r).name);
            }

            //执行后
            protected void afterExecute(Runnable r, Throwable t) {
                System.out.println("执行完成：" + ((MyTask) r).name);
            }

            //shutdown
            protected void terminated( ) {
                System.out.println("线程池退出");
            }

        };
        for (int i = 0; i < 5; i++) {
            MyTask task = new MyTask("TASK-" + i);
            es.execute(task);
            Thread.sleep(10);
        }
        es.shutdown();
    }
}
