package com.example.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;


public class CuratorClientUtils {

    private final static String CONNECTSTRING = "localhost:2181";


    public static CuratorFramework getInstance( ) {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.
                newClient(CONNECTSTRING, 5000, 5000,
                        new ExponentialBackoffRetry(1000, 3));
        curatorFramework.start();
        return curatorFramework;
    }
}
