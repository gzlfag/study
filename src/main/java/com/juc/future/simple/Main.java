package com.juc.future.simple;

/**
 * 调用client发出请求
 * @author Administrator
 *
 */
public class Main {
    public static void main(String[] args) {
        Client client = new Client();
        
        //这里会立即返回,因为得到的是FutureData而不是RealData
        Data data = client.request("a");
        System.out.println("请求完毕");
        try {
        	//这里可以用一个sleep代替了对其它业务逻辑的处理
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        
        //使用真实的数据,使用getResult就会阻塞到真是数据返回为止
        System.out.println("数据 = " + data.getResult());
    }
}
