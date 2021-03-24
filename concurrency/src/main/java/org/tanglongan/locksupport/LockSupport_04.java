package org.tanglongan.locksupport;

import java.util.concurrent.locks.LockSupport;

/**
 * 当线程在没有持有许可证的情况下，调用park()方法而被阻塞挂起时，这个blocker对象会被保存到该线程内部。
 * 使用诊断工具可以观察线程被阻塞的原因，诊断工具是通过调用getBlocker（Thread）方法来获取blocker对象的，所以JDK推荐我们使用带有blocker参数的park方法，并且blocker被设置为this，这样当在打印线程堆栈排查问题时就能知道是哪个类被阻塞了。
 */
public class LockSupport_04 {

    /**
     * 方法调用对象将当前线程阻塞挂起，同时将当前对象设置到线程中，表示线程是被当前对象或类阻塞的
     */
    public void testPark() {
        LockSupport.park(this);
    }

    public static void main(String[] args) {
        LockSupport_04 lock = new LockSupport_04();
        lock.testPark();
    }

}
