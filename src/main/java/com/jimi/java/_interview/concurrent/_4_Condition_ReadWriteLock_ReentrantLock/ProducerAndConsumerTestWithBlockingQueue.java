package com.jimi.java._interview.concurrent._4_Condition_ReadWriteLock_ReentrantLock;

import sun.nio.ch.ThreadPool;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 生产者-消费者模型实现
 * 生产者-消费者问题，其实就是阻塞问题，
 * 如何控制队列长度，以及如何匹配生产和消费的速度？？？
 * 有两种实现方式：1、直接使用Lock和Condition的方式;
 * 2、使用阻塞队列，put和take方法生产和消费队列元素，其实这两个方法内部也是采用Condition方式实现
 *
 * @author jimi
 * @version 2017-01-03 17:35.
 */
public class ProducerAndConsumerTestWithBlockingQueue {

    BlockingQueue<Integer> queue = new LinkedBlockingDeque<>(20);

    private void test() {
        /*
        注：参阿里巴巴开发手册，使用线程池时，不建议使用Executors，容易造成内存溢出。
        而是使用ThreadPoolExecutor
         */
        ExecutorService executor = Executors.newCachedThreadPool();

        /*LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1, TimeUnit.DAYS, queue);*/

        for (int i = 0; i < 5; ++i) {
            executor.submit(new Producer(i));
            executor.submit(new Consumer(i));
        }

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdownNow();
    }

    public static void main(String[] args) {
        new ProducerAndConsumerTestWithBlockingQueue().test();
    }

    class Producer implements Runnable{

        private int pid ;

        public Producer(int pid) {
            this.pid = pid;
        }

        @Override
        public void run() {
            while (true && !Thread.currentThread().isInterrupted()){
                try {
                    Random random = new Random();
                    queue.put(random.nextInt());

                    System.out.println("Producer" + pid + " add " + pid + " size: " + queue.size());

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Consumer implements Runnable{
        private int cid;

        public Consumer(int cid) {
            this.cid = cid;
        }


        @Override
        public void run() {
            while (true && !Thread.currentThread().isInterrupted()){
                try {
                    queue.take();
                    System.out.println("Consumer" + cid + " consuming data " + cid +" data size : " + queue.size());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //主要用于方便更好观察
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
