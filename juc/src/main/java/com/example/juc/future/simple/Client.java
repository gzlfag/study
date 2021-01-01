package com.example.juc.future.simple;

/**
 * 返回Data对象.立即返回FutureData,开启clientThread线程装配RealData
 */
public class Client {
    public Data request(final String queryStr) {
        final FutureData future = new FutureData();
        // RealData的构建很慢,所以异步执行
        new Thread(( ) -> {
            RealData realdata = new RealData(queryStr);
            future.setRealData(realdata);
        }).start();

        //构建完成后立即返回,future可能是空的
        return future;
    }
}
