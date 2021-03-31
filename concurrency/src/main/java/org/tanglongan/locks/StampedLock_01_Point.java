package org.tanglongan.locks;

import java.util.concurrent.locks.StampedLock;

public class StampedLock_01_Point {

    private double x, y;
    private final StampedLock lock = new StampedLock();

    /**
     * 移动
     */
    void move(double deltaX, double deltaY) { //多个线程调用该函数，修改x,y的值
        long stamp = lock.writeLock(); //涉及对共享资源的修改，使用写锁-独占操作
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * 使用乐观读锁访问共享资源
     * 注意：乐观读在保证数据一致性上需要拷贝一份要操作的变量到方法栈，并且在操作数据时候可能其他写线程已经修改了数据
     * 而我们操作的是方法栈里面的数据，也就是一个快照，所以最多返回的不是最新的数据，但是一致性还是得到保障的
     */
    double distanceFromOrigin() {
        long stamp = lock.tryOptimisticRead();  //使用乐观读锁
        double currentX = x, currentY = y;      //将共享变量拷贝到线程栈
        if (!lock.validate(stamp)) {            //读数据期间有其他线程修改了数据，读的是脏数据，丢弃
            stamp = lock.readLock();            //升级为“悲观读”
            try {
                currentX = x;
                currentY = y;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }


    void moveIfOrigin(double newX, double newY) {
        long stamp = lock.readLock();
        try {
            while (x == 0.0 && y == 0.0) {
                long ws = lock.tryConvertToWriteLock(stamp);    //读作转化为写锁
                if (ws != 0L) {
                    stamp = ws;
                    x = newX;
                    y = newY;
                } else {
                    lock.unlockRead(stamp);
                    stamp = lock.writeLock();
                }
            }
        } finally {
            lock.unlock(stamp);
        }
    }


}
