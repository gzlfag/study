package com.example.javaapi.lock;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperClient {

    private final static String CONNECTSTRING = "localhost:2181";

    private static int sessionTimeout = 5000;

    //获取连接
    public static ZooKeeper getInstance( ) throws IOException, InterruptedException {
        final CountDownLatch conectStatus = new CountDownLatch(1);
        ZooKeeper zooKeeper = new ZooKeeper(CONNECTSTRING, sessionTimeout, event -> {
            if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                conectStatus.countDown();
            }
        });
        conectStatus.await();
        return zooKeeper;
    }

    public static int getSessionTimeout( ) {
        return sessionTimeout;
    }
}
