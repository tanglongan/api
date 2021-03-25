package org.tanglongan.locks;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 基于AQS实现一个不可重入的独占锁
 * 定义了一个内部类用来实现具体的锁的操作，Sync继承了AQS。由于这里实现的是独占模式的锁，所以Sync重写了tryAcquire、tryRelease和isHeldExeclusively 3个方法
 * 另外Sync提供了一个newCondition这个方法用来支持条件变量
 */
public class ReentrantLock_02 implements Lock, Serializable {

    private static class Sync extends AbstractQueuedSynchronizer {
        /**
         * 锁是否已经被持有
         */
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        /**
         * 尝试获取锁
         *
         * @param acquires 锁状态增量值
         */
        @Override
        protected boolean tryAcquire(int acquires) {
            assert acquires == 1;
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        /**
         * 尝试释放锁（设置state为0，并且锁的持有者设为空）
         *
         * @param acquires 锁状态增量值
         */
        @Override
        protected boolean tryRelease(int acquires) {
            assert acquires == 1;
            if (getState() == 0) {
                throw new IllegalMonitorStateException();
            }
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        /**
         * 提供条件变量
         */
        public Condition newCondition() {
            return new ConditionObject();
        }
    }

    private final Sync sync = new Sync();

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }

}
