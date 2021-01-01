package com.example.juc.singleton;

/**
 * 懒加载的单例模式(简单单例模式的改进)
 * 
 * 缺点:
 * 高并发的时候对性能有影响
 * 因为synchronized
 *
 */
public class LazySingleton {
	private LazySingleton() {
		System.out.println("LazySingleton is create");
	}

	private static LazySingleton instance = null;

	public static synchronized LazySingleton getInstance() {
		if (instance == null)
			instance = new LazySingleton();
		return instance;
	}
}