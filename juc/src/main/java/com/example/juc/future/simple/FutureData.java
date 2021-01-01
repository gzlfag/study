package com.example.juc.future.simple;


/**
 * realdata的包装类
 * Future数据,构造很快,但是是一个虚拟的数据,需要装配RealData
 */
public class FutureData implements Data {
    protected RealData realdata = null;
    protected boolean isReady = false;

    public synchronized void setRealData(RealData realdata) {
        if (isReady) {
            return;
        }
        this.realdata = realdata;
        isReady = true;
        notifyAll();    //realData已经被注入,通知getResult
    }

    //会等待realData构造完成
    public synchronized String getResult( ) {
        while (!isReady) {
            try {
                wait();        //一直等待直到realData构造完成
            } catch (InterruptedException e) {
                e.getStackTrace();
            }
        }
        return realdata.result;        //由realData实现
    }
}
