package org.tanglongan.threadpool;

import java.security.AccessController;
import java.util.concurrent.*;

public class ThreadTest_01 {


    public static void main(String[] args) throws ExecutionException, InterruptedException {
/*        Thread thread = new MyThread();
        thread.start();

        Thread thread2 = new Thread(new MyRunnable());
        thread2.start();*/

        FutureTask<String> futureTask = new FutureTask<>(new MyCallable());
        Thread thread3 = new Thread(futureTask);
        thread3.start();
        System.out.println(futureTask.get());

    }


}


class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("I am running...");
    }
}


class MyRunnable implements Runnable {

    @Override
    public void run() {
        System.out.println("I am running...");
    }
}

class MyCallable implements Callable<String> {

    @Override
    public String call() {
        System.out.println("I am running...");
        return "hello world";
    }

}