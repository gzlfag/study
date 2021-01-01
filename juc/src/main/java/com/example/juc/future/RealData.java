package com.example.juc.future;

import java.util.concurrent.Callable;

/**
 * 实现了Callable(单方法call,有返回值)
 */
public class RealData implements Callable<String> {
    private String para;

    public RealData(String para) {
        this.para = para;
    }

    public String call( ) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(para);
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        return sb.toString();
    }
}
