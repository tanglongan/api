package org.tanglongan.locksupport;

import java.util.concurrent.locks.LockSupport;

public class LockSupport_02 {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            System.out.println("Begin park!");
            //调用park()方法将自己挂起
            LockSupport.park();
            System.out.println("End park！");
        });

        //启动子线程
        thread.start();
        //主线程休眠1秒
        Thread.sleep(1000);
        System.out.println("main thread begin unpark!");
        //主线程调用unpark()方法让线程thread持有许可证，然后park方法返回
        LockSupport.unpark(thread);
    }


}
