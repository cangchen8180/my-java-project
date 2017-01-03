package com.jimi.java._interview.concurrent._4_Condition_ReadWriteLock_ReentrantLock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jimi
 * @version 2016-12-30 17:58.
 */
public class ConditionSimpleTest implements Runnable{

    private static ReentrantLock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();

    public static void main(String[] args) {
        ConditionSimpleTest conditionTest = new ConditionSimpleTest();
        new Thread(conditionTest).start();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*
        Condition（也称为条件队列 或条件变量）为线程提供了一种手段，在某个状态条件下直到接到另一个线程的通知，一直处于挂起状态（即“等待”）。因为访问此共享状态信息发生在不同的线程中，所以它必须受到保护，因此要将某种形式的锁与 Condition相关联。
        Condition 实例实质上被绑定到一个锁上。要为特定 Lock 实例获得 Condition 实例，可以使用其 newCondition() 方法
         */
        lock.lock();
        condition.signal();
        lock.unlock();
    }

    @Override
    public void run() {
        //加锁
        lock.lock();

        try {
            condition.await();

            System.out.println(Thread.currentThread().getName() + "线程等待结束");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}
