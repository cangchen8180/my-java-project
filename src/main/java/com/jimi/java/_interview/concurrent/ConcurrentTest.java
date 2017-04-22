package com.jimi.java._interview.concurrent;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by lagou on 2017/4/20.
 */
public class ConcurrentTest {

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //在解锁时，会看是否还有等待线程，然后唤醒head指向的节点
        lock.unlock();
    }
}
