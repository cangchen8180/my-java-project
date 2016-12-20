package com.jimi.java.concurrent;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @author jimi
 * @description
 *  闭锁
 * <p>该部分使用了两个闭锁：
 *      一个锁用于保证等所有线程都准备就绪，才让所有线程同时开始工作;
 *      另一个锁用于保证所有的线程都处理完成，才让程序继续向下执行。
 * </p>
 * <p>由一个startGate拦住所有线程的执行，当所有线程就绪完成后调用countDown将它们释放，
 *  而另一扇大门——endGate后面正等着计算执行时间，而endGate等待的事件由这些线程触发</p>
 * @date 2016-02-02 19:28.
 */
public class TestCountDownLatch {

    private final static int START_LATCH_FLAG = 1;
    private static int N = 0;
    /**
     * 闭锁测试多线程处理的方法
     * @param nThreads
     * @param task
     * @return
     * @throws InterruptedException
     */
    public static long latch(int nThreads, final Runnable task) {

        final CountDownLatch startLatch = new CountDownLatch(START_LATCH_FLAG);
        final CountDownLatch endLatch = new CountDownLatch(nThreads);

        for(int i = 0; i < nThreads; i++){
            //启动相应数量线程
            new Thread(new Runnable() {
                public void run() {
                    int j = N++;
                    try {
                        //所有线程准备好之前，先准备就绪的线程处于等待状态
                        startLatch.await();
                        System.out.println("Thread[" + j + "] start... ");
                        //回调执行任务
                        try {
                            task.run();
                        }finally {
                            //任务执行后，告诉结束闭锁对象
                            endLatch.countDown();
                            System.out.println("Thread[" + j + "] finish... ");
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        long startTime = System.nanoTime();
        //等所有线程准备就绪，开锁让线程工作
        startLatch.countDown();
        System.out.println("All Thread start... ");
        //闭锁等所有线程处理完再让程序向下执行
        try {
            endLatch.await();
            System.out.println("All Thread finish... ");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long endTime = System.nanoTime();

        return endTime - startTime;
    }

    public static void main(String[] args){
        System.out.println("10个线程共用时=" + TestCountDownLatch.latch(10, new Runnable() {
            public void run() {
                Random random = new Random();
                int num = random.nextInt();
                System.out.println("Random num ::"+ num);
                try {
                    Thread.sleep(1000);
                    System.out.println("Thread sleep...first... ");
                    if(num < 50){
                        Thread.sleep(1000);
                        System.out.println("Thread sleep...（num < 50） ");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }));
    }


}
