package com.example.juc.productAndConsumer;

import java.text.MessageFormat;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 * 消费者
 */
public class Consumer implements Runnable {
    // 共享队列
    private BlockingQueue<PCData> queue;
    private static final int SLEEPTIME = 1000;

    Consumer(BlockingQueue<PCData> queue) {
        this.queue = queue;
    }

    public void run( ) {
        System.out.println("start Consumer id=" + Thread.currentThread().getId());
        Random r = new Random();

        try {
            while (true) {
                //提取任务
                PCData data = queue.take();
                if (null != data) {
                    //计算平放
                    int re = data.getData() * data.getData();
                    System.out.println(MessageFormat.format("{0}*{1}={2}", data.getData(), data.getData(), re));
                    Thread.sleep(r.nextInt(SLEEPTIME));
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
