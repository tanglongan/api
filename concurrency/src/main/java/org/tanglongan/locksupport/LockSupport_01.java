package org.tanglongan.locksupport;

import java.util.concurrent.locks.LockSupport;

public class LockSupport_01 {

    public static void main(String[] args) {
        System.out.println("Begin park!");
        //使得当前线程获得许可证
        LockSupport.unpark(Thread.currentThread());
        //再次调用park()方法去获取许可证
        LockSupport.park();
        System.out.println("End park!");
    }

}
