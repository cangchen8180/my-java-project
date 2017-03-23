package com.jimi.java._interview.concurrent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author jimi
 * @version 2017-01-06 00:29.
 */
public class GsonConcurrentTest implements Runnable{

    static ThreadLocal<Gson> gsonThreadLocal = new ThreadLocal<>();
    static {
        gsonThreadLocal.set(new GsonBuilder().create());
    }

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        // ============================ ReentrantLock(可重入锁) ============================
        ReentrantLock lock = new ReentrantLock();

        lock.tryLock();

        lock.lock();

        lock.lockInterruptibly();

        lock.unlock();

        Condition notFull = lock.newCondition();
        Condition notEmpty = lock.newCondition();

        notFull.await();

        notFull.signal();

        notFull.signalAll();

        // ============================ ReentrantReadWriteLock(读写锁) ============================
        ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

        reentrantReadWriteLock.readLock();

        reentrantReadWriteLock.writeLock();



        // ============================ CountDownLatch ============================
        CountDownLatch countDownLatch = new CountDownLatch(3);

        countDownLatch.countDown();

        countDownLatch.await();

        // ============================ CyclicBarrier ============================
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3);

        cyclicBarrier.await();

    }

    @Override
    public void run() {

    }
}
