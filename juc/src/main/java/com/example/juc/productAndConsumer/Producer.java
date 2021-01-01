package com.example.juc.productAndConsumer;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生产者
 */
public class Producer implements Runnable {
    //停止标志
    private volatile boolean isRunning = true;
    //共享队列
    private BlockingQueue<PCData> queue;
    //计数
    private static AtomicInteger count = new AtomicInteger();
    private static final int SLEEPTIME = 1000;

    public Producer(BlockingQueue<PCData> queue) {
        this.queue = queue;
    }

    public void run( ) {
        PCData data;
        Random r = new Random();

        System.out.println("start producer id=" + Thread.currentThread().getId());
        try {
            while (isRunning) {
                Thread.sleep(r.nextInt(SLEEPTIME));
                //构造任务数据
                data = new PCData(count.incrementAndGet());
                System.out.println(data + " is put into queue");
                //提交数据到缓冲区,时限限制2s
                if (!queue.offer(data, 2, TimeUnit.SECONDS)) {
                    System.err.println("failed to put data:" + data);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public void stop( ) {
        isRunning = false;
    }
}