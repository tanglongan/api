package org.tanglongan.locks;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLock_01 implements Runnable {

    private static final ReentrantLock lock = new ReentrantLock();
    private static int a = 0;

    @Override
    public void run() {
        try {
            //加锁
            lock.lock();
            //每个线程对变量a进行累加操作
            for (int i = 0; i < 100000; i++) {
                a++;
            }
        } finally {
            //解锁
            lock.unlock();
        }
    }


    public static void main(String[] args) throws InterruptedException {
        ReentrantLock_01 r = new ReentrantLock_01();
        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("变量a=> " + a);
    }
}
