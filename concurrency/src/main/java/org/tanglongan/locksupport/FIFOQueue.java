package org.tanglongan.locksupport;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

public class FIFOQueue {

    private final AtomicBoolean locked = new AtomicBoolean(false);
    private final Queue<Thread> waiters = new ConcurrentLinkedQueue<>();

    /**
     * 获取锁
     */
    public void lock() {
        boolean wasInterrupted = false;
        Thread currentThread = Thread.currentThread();
        waiters.add(currentThread);
        //只有队首的线程可以获取到锁
        while (waiters.peek() != currentThread || !locked.compareAndSet(false, true)) {
            LockSupport.park(currentThread);
            //如果park方法是因为被中断而返回，则忽略中断，并且重置中断标志，做个标记。
            //然后再次判断当前线程是不是队首元素或者当前锁是否已经被其他线程获取，如果是则继续调用park方法挂起自己。
            if (Thread.interrupted()) {
                wasInterrupted = true;
            }
        }
        waiters.remove();
        //如果标记为true则中断该线程，这个怎么理解呢？
        //其实就是其他线程中断了该线程，虽然我对中断信号不感兴趣，忽略它，但是不代表其他线程对该标志不感兴趣，所以要恢复下。
        if (wasInterrupted) {
            currentThread.interrupt();
        }
    }

    /**
     * 释放锁
     */
    public void unlock() {
        locked.set(false);
        LockSupport.unpark(waiters.peek());
    }

}
