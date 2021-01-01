package com.example.juc.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 拒绝策略,任务不执行了,用什么方式把它回绝掉
 *
 * @author Administrator
 */
public class RejectThreadPoolDemo {
    public static class MyTask implements Runnable {

        public void run( ) {
            System.out.println(System.currentTimeMillis() + ":Thread ID:"
                    + Thread.currentThread().getId());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyTask task = new MyTask();
        ExecutorService es = new ThreadPoolExecutor(5, 5,
                0L, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(),   //没人拿的时候就会被拒绝
                Executors.defaultThreadFactory(),
                (r, executor) -> {
                    //简单的打印任务
                    System.out.println(r.toString() + " is discard");
                });
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            es.submit(task);
            Thread.sleep(10);
        }
    }
}
