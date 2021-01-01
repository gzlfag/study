package com.example.juc.threadpool;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 调度任务线程池
 */
public class ScheduledExecutorServiceDemo {
    public static void main(String[] args) {
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(10);
        // 如果前面的任务没有完成，则调度也不会启动
        ses.scheduleWithFixedDelay(( ) -> {
            try {
                Thread.sleep(1000);
                System.out.println(System.currentTimeMillis() / 1000);
                // if(System.currentTimeMillis()%2==0){
                // System.out.println("exception");
                // throw new RuntimeException();
                // }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 0, 2, TimeUnit.SECONDS);        //每隔2S  要前面的完成后才执行后面的,所以这里是隔3s

    }
}
