package com.example.juc.threadpool;

/**
 * 使用单线程池和直接开启线程的差别
 *
 * @author Administrator
 */
public class TestThreadPool {
    public static class MyThread implements Runnable {
        protected String name;

        public MyThread( ) {
        }

        public MyThread(String name) {
            this.name = name;
        }

        public void run( ) {
            try {
                Thread.sleep(100);
                //System.out.print(name+" ");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            ThreadPool.getInstance().start(new MyThread("testThreadPool" + Integer.toString(i)));
        }

        long endTime = System.currentTimeMillis();
        System.out.println("testThreadPool" + ": " + (endTime - startTime));
        System.out.println("getCreatedThreadsCount:" + ThreadPool.getInstance().getCreatedThreadsCount());
        Thread.sleep(1000);
    }


}
