package org.tanglongan.locks;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 通过一个世纪的例子来解释Condition的用法
 * 题目：打印1到9的数字，由线程A先打印1，2，3，然后线程B打印4,5,6，然后再由线程A打印7，8，9
 * 分析：这道题目有很多解法，这里使用Condition解决
 */
public class ReentrantLock_02_Condition_Usage_1 {

    /**
     * NumberWrapper只是为了封装一个数字，以便于可以将数字对象共享，并设置为final
     * 注意这里不能是Integer，Integer是不可变对象
     */
    static class NumberWrapper {
        public int value = 1;
    }

    public static void main(String[] args) {
        //初始化变量
        final NumberWrapper num = new NumberWrapper();
        //初始化可重复锁
        final Lock lock = new ReentrantLock();
        //第一个条件当屏幕上输出到3
        final Condition reachThreeCondition = lock.newCondition();
        //第二个条件屏幕上输出到6
        final Condition reachSixCondition = lock.newCondition();

        Thread threadA = new Thread(() -> {
            lock.lock();
            try {
                System.out.println("Thread-A start write-------");
                while (num.value <= 3) {
                    System.out.println(num.value);
                    num.value++;
                }
                reachThreeCondition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

            lock.lock();
            try {
                reachSixCondition.await();
                System.out.println("Thread-A start write-------");
                while (num.value <= 3) {
                    System.out.println(num.value);
                    num.value++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        });


        Thread threadB = new Thread(() -> {
            try {
                lock.lock();
                while (num.value <= 3) {
                    //等待3输出完毕的信号
                    reachThreeCondition.await();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

            try {
                lock.lock();
                System.out.println("Thread-B start write-------");
                while (num.value <= 6) {
                    System.out.println(num.value);
                    num.value++;
                }
                reachSixCondition.signalAll();
            } finally {
                lock.unlock();
            }
        });


    }


}
