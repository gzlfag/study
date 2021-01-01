package com.example.juc.singleton;

/**
 * 单例模式的简单实现
 * 线程安全的
 * 缺点:何时产生示例不好控制(访问静态字段就会创建)
 */
public class Singleton {
	public static int STATUS = 1;

	private Singleton() {
		System.out.println("Singleton is create"); // 创建单例的过程可能会比较慢
	}

	private static Singleton instance = new Singleton();

	public static Singleton getInstance() {
		return instance;
	}
}