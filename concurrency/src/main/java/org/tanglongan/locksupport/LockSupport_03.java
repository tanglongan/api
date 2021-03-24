package org.tanglongan.locksupport;

import java.util.concurrent.locks.LockSupport;

public class LockSupport_03 {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            System.out.println("Child thread begin park!");
            //调用park方法挂起自己，只有被中断才退出循环
            while (!Thread.currentThread().isInterrupted()) {
                LockSupport.park();
            }
            System.out.println("Child thread end park！");
        });

        //启动子线程
        thread.start();
        //主线程休眠1秒
        Thread.sleep(1000);
        System.out.println("main thread begin unpark!");
        //中断子线程
        thread.interrupt();
    }
}
