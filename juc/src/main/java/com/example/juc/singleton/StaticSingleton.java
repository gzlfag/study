package com.example.juc.singleton;

/**
 * 静态内部类单例模式   延迟加载
 * <p>
 * 比懒加载的好处是 没有 synchronized
 * 高并发下面性能好点
 */
public class StaticSingleton {
    public static int STATUS;

    private StaticSingleton( ) {
        System.out.println("StaticSingleton is create");
    }

    private static class SingletonHolder {
        private static StaticSingleton instance = new StaticSingleton();
    }

    public static StaticSingleton getInstance( ) {
        return SingletonHolder.instance;
    }
}