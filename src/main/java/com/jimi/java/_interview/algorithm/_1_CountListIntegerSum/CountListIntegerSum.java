package com.jimi.java._interview.algorithm._1_CountListIntegerSum;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 一个10w个整数的列表，充分利用cpu性能，尽可能快的计算出列表的总和
 *
 * @author jimi
 * @version 2016-12-26 22:58.
 */
public class CountListIntegerSum {

    /*
    可用cpu数
     */
    private int threadCount = Runtime.getRuntime().availableProcessors();
    private ExecutorService executor = Executors.newFixedThreadPool(threadCount);

    public CountListIntegerSum() {
    }

    public long countSum(List<Integer> list) {
        int len = list.size() / threadCount;

        //用于并发情况下，同时操作sumList的情况
        final CopyOnWriteArrayList<Long> sumList = new CopyOnWriteArrayList<>();
        final CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final List<Integer> subList;
            int fromIndex = i * len;
            final int index = i + 1;
            int toIndex = index * len;
            subList = list.subList(fromIndex, toIndex);
            System.out.println("[subList]index=" + index + ", size=" + subList.size() + ", values= " + subList
                    .toString());

            //所以不需要返回值方式
            Future<Long> future = executor.submit(new Callable<Long>() {
                private Long subSum = 0l;

                @Override
                public Long call() throws Exception {
                    try {
                        for (Integer i : subList) {
                            subSum += i;
                        }

                        //用于测试多线程是否并发执行
                        Thread.sleep(3000);

                        System.out.println("[subList]index=" + index + ", subSum = " + subSum);
                        //并发下
                        sumList.add(subSum);
                        return subSum;
                    }finally {
                        endLatch.countDown();
                    }

                }
            });

            /*
            注：如果使用下面方式，获取到返回值后，再往下执行的话，就变成了顺序执行，也就未发挥多线程的并发性。
             */
            /*try {
                sumList.add(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }*/
        }

        //使用闭锁，确保所有线程都执行，再计算总和
        try {
            endLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("start count subSum");
        long sum = 0;
        for (int i = 0; i < sumList.size(); i++) {
            sum += sumList.get(i);
        }
        executor.shutdown();
        return sum;
    }


    public static void main(String[] args) {
        int initCap = 100000;
        List<Integer> integerList = new ArrayList<>(initCap);
        for (int i = 0; i < initCap; i++) {
            integerList.add(i + 1);
        }

        CountListIntegerSum countListIntegerSum = new CountListIntegerSum();
        long sum = countListIntegerSum.countSum(integerList);

        //正确结果：5000050000
        System.out.println("sum = " + sum);
    }
}
