package com.jimi.java._interview.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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


        //--------------------ThreadPoolExecutor-------------------
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        /**
         * 最大线程数的设置，以及IO密集型和CPU密集型来区别对待
         * IO密集型，可将maximumPoolSize设置大点，因为线程创建和释放资源的代价肯定远远小于IO操作
         */
        int maximumPoolSize = availableProcessors * 2;
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(availableProcessors, maximumPoolSize, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000), new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                System.out.println("线程池拒绝处理...finished");
            }
        });

        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程...running...finished");
            }
        });
        threadPoolExecutor.shutdownNow();
    }
}
