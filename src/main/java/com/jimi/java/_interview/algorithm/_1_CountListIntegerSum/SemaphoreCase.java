package com.jimi.java._interview.algorithm._1_CountListIntegerSum;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * @author jimi
 * @version 2016-12-27 22:59.
 */
public class SemaphoreCase {
    private static final int THREAD_COUNT = 30;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_COUNT);
    private static Semaphore s = new Semaphore(3);

    public static void main(String[] args) {
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int finalI = i;
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.currentThread().setName("executeThread-" + finalI);
                        long start = System.currentTimeMillis();
                        s.acquire();
                        Thread.sleep(1000);
                        System.out.println("save data");
                        s.release();
                        System.out.println(Thread.currentThread().getName() + " 耗时=" + (System.currentTimeMillis() -
                                start) + "ms");
                    } catch (InterruptedException e) {
                    }
                }
            });
        }
        threadPool.shutdown();
    }
}
