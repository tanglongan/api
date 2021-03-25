package org.tanglongan.locks;

import com.alibaba.fastjson.JSON;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;

/**
 * 使用自定义的ReentrantLock_02独占锁来实现一个生产者-消费者模型
 */
public class ReentrantLock_03_Custom_Usage {

    private static final ReentrantLock_03_Custom lock = new ReentrantLock_03_Custom();
    private static final Condition notFull = lock.newCondition();
    private static final Condition notEmpty = lock.newCondition();
    private static final Queue<String> queue = new LinkedBlockingQueue<>();
    private static final int queueSize = 10;

    public static void main(String[] args) {
        Thread producerThread = new Thread(() -> {
            System.out.println("线程[ " + Thread.currentThread().getName() + " ]开始执行");
            //获取独占锁
            lock.lock();
            try {
                //如果队列满了，则等待
                while (queue.size() == queueSize) {
                    notEmpty.await();
                }
                System.out.println("开始生产数据...");
                //生产数据
                int i = 0;
                while ((i++) < queueSize) {
                    queue.add("e-" + i);
                }
                System.out.println("生产数据完成，此时队列中数据======>" + JSON.toJSONString(queue.toArray()));
                //唤醒消费者线程
                notFull.signalAll();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }, "生产者");


        Thread consumerThread = new Thread(() -> {
            System.out.println("线程[ " + Thread.currentThread().getName() + " ]开始执行");
            lock.lock();
            try {
                //队列空，则等待
                while (0 == queue.size()) {
                    notFull.await();
                }
                //消费数据
                System.out.println("开始消费数据...");
                int i = 0;
                while (i < queueSize) {
                    String data = queue.poll();
                    System.out.println("消费======>" + data);
                    i++;
                }
                //唤醒生产者线程
                notEmpty.signalAll();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }, "消费者");

        consumerThread.start();
        producerThread.start();
    }

}
