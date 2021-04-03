package com.example.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CuratorOperatorDemo {

    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorClientUtils.getInstance();
        System.out.println("连接成功.........");

        //fluent风格

        /**
         * 创建持久化节点
         */
        try {
            String result = curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).
                    forPath("/curator/curator1/curator11", "123".getBytes());

            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            /**
             * 删除节点
             */
//            curatorFramework.delete().deletingChildrenIfNeeded().forPath("/curator");
        }

        /**
         * 查询
         */
        Stat stat = new Stat();
        try {
            byte[] bytes = curatorFramework.getData().storingStatIn(stat).forPath("/curator");
            System.out.println("查询:" + new String(bytes) + "-->stat:" + stat);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 更新
         */
        try {
            stat = curatorFramework.setData().forPath("/curator", "123".getBytes());
            System.out.println("更新:" + stat);
        } catch (Exception e) {
            e.printStackTrace();
        }


        /**
         * 异步操作
         */
        ExecutorService service = Executors.newFixedThreadPool(1);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).
                    inBackground(new BackgroundCallback() {
                        @Override
                        public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                            System.out.println(Thread.currentThread().getName() + "->resultCode:" + curatorEvent.getResultCode() + "->"
                                    + curatorEvent.getType());
                            countDownLatch.countDown();
                        }
                    }, service).forPath("/mj", "123".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        countDownLatch.await();
        service.shutdown();

        /**
         * 事务操作（curator独有的）
         */
        try {
            Collection<CuratorTransactionResult> resultCollections = curatorFramework.inTransaction()
                    .create().forPath("/trans", "111".getBytes()).and()
                    .create().forPath("/trans", "111".getBytes()).and()   //这里报错,上面一步也失败
                    .setData().forPath("/curator", "111".getBytes()).and().commit();
            for (CuratorTransactionResult result : resultCollections) {
                System.out.println(result.getForPath() + "->" + result.getType() + "->" + result.getResultPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            curatorFramework.delete().forPath("/trans");
            curatorFramework.delete().forPath("/curator");
        }
    }
}
