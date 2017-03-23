package com.jimi.java._interview.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * sleep()方法：让出CPU，但不会释放当前对象的锁。
 * await()方法会释放当前对象的锁，然后进入和对象相关的等待线程池中，这样其他同步线程就可以继续执行。
 * Created by lagou on 2017/3/23.
 */
public class SleepAndAwaitTest {
    private ReentrantLock lock = new ReentrantLock();
    private Condition trip = lock.newCondition();

    private void await() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + ": =========== lock ===========");
//            for (;;) {
            //wait()方法会释放当前对象的锁，其他线程可以执行，但本线程会阻塞在这里
                trip.await();
            System.out.println(Thread.currentThread().getName() + ": =========== await end ===========");

            //sleep()方法不会释放当前对象锁，所以其他线程要等该线程执行完才会执行。
//            Thread.sleep(5000);
//            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println(Thread.currentThread().getName() + ": =========== unlock ===========");
            lock.unlock();
        }

    }

    public static void main(String[] args) {
        final SleepAndAwaitTest awaitTest = new SleepAndAwaitTest();
        ExecutorService executorService =
                Executors.newFixedThreadPool(3);

        for (int i = 0; i < 3; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    awaitTest.await();
                }
            });
        }
    }
}


