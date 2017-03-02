## CountDownLatch、CyclicBarrier和Semaphore的区别

### 综述

CountDownLatch和CyclicBarrier都是用于线程同步，Semaphore用于控制被共享资源的访问（如数据库连接数）。

- CountDownLatch : 一般用于某个线程A等待若干个其他线程执行完任务之后，它才执行。

    > 比如 **赛跑**，每个运动员看做一个子线程，裁判就是主线程，裁判发令（设置一个值为1的计数器，发令之前所有子线程await等待命令，裁判员发令让计数置为0，所有子线程同时开跑）所有运动员开跑后，需要等待所有人跑完再统计成绩（设置一个值为运动员数目的计数器，所有运动员开跑后裁判await被阻塞，每个运动员跑完的时候countDown()一下，所有运动员跑完计数达到0，裁判释放阻塞才根据每个运动员的用时分别计分等做进一步处理）。

- CyclicBarrier : N个线程相互等待，所有线程都完成之前，其他完成的线程都必须等待。

    > 比如 **团队旅游**，一个团队通常分为几组，每组人走的路线可能不同，但都需要到达某一地点等待团队其它成员到达后才能进行下一站。

- Semaphore：可以控制某个资源可被同时访问的个数，通过 acquire() 获取一个许可，如果没有就等待，而 release() 释放一个许可。

    > 比如 **特别公用资源有限的应用场景，比如数据库连接**。假如有一个需求，要读取几万个文件的数据，因为都是IO密集型任务，我们可以启动几十个线程并发的读取，但是如果读到内存后，还需要存储到数据库中，而数据库的连接数只有10个，这时我们必须控制只有十个线程同时获取数据库连接保存数据，否则会报错无法获取数据库连接。这个时候，我们就可以使用Semaphore来做流控。
    
### 使用
针对求和10w个整数列表的两种实现

#### CountDownLatch

一般用于某个线程A等待若干个其他线程执行完任务之后，它才执行。

比如 **赛跑**，每个运动员看做一个子线程，裁判就是主线程，裁判发令（设置一个值为1的计数器，发令之前所有子线程await等待命令，裁判员发令让计数置为0，所有子线程同时开跑）所有运动员开跑后，需要等待所有人跑完再统计成绩（设置一个值为运动员数目的计数器，所有运动员开跑后裁判await被阻塞，每个运动员跑完的时候countDown()一下，所有运动员跑完计数达到0，裁判释放阻塞才根据每个运动员的用时分别计分等做进一步处理）。

```java
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
```

#### CyclicBarrier

N个线程相互等待，所有线程都完成之前，其他完成的线程都必须等待。

比如 **团队旅游**，一个团队通常分为几组，每组人走的路线可能不同，但都需要到达某一地点等待团队其它成员到达后才能进行下一站。

```java
public class CountListIntegerSumForCyclicBarrier {

    /*
    可用cpu数
     */
    private int threadCount = Runtime.getRuntime().availableProcessors();
    private ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    private long sum;

    public CountListIntegerSumForCyclicBarrier() {
    }

    public long countSum(List<Integer> list) {
        int len = list.size() / threadCount;

        //+1原因：
        //用于控制main主线程的，主线程也要等待，它要等待其他所有的线程完成才能输出sum值，这样才能保证sum值的正确性，如果main不等待的话，那么结果将是不可预料的。
        final CyclicBarrier barrier = new CyclicBarrier(threadCount + 1);

        for (int i = 0; i < threadCount; i++) {
            final List<Integer> subList;
            int fromIndex = i * len;
            final int index = i + 1;
            int toIndex = index * len;
            subList = list.subList(fromIndex, toIndex);
            System.out.println("[subList]index=" + index + ", size=" + subList.size() + ", values= " + subList
                    .toString());

            //所以不需要返回值方式
            executor.execute(new Runnable() {
                private Long subSum = 0l;

                @Override
                public void run() {
                    for (Integer i : subList) {
                        subSum += i;
                    }

                    //用于测试多线程是否并发执行
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("[subList]index=" + index + ", subSum = " + subSum);

                    //在CountListIntegerSum对象上加锁
                    synchronized (this) {
                        sum += subSum;
                    }
                    try {
                        barrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        executor.shutdown();

        //注：用于等待所有计算线程执行完成，再返回值，否则，会出现不可预料的错误。
        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

        return sum;
    }


    public static void main(String[] args) {
        int initCap = 100000;
        List<Integer> integerList = new ArrayList<>(initCap);
        for (int i = 0; i < initCap; i++) {
            integerList.add(i + 1);
        }

        CountListIntegerSumForCyclicBarrier countListIntegerSum = new CountListIntegerSumForCyclicBarrier();
        long sum = countListIntegerSum.countSum(integerList);

        //正确结果：5000050000
        System.out.println("sum = " + sum);
    }
}
```

#### Semaphore
Semaphore可以用于做流量控制，特别公用资源有限的应用场景，比如数据库连接。假如有一个需求，要读取几万个文件的数据，因为都是IO密集型任务，我们可以启动几十个线程并发的读取，但是如果读到内存后，还需要存储到数据库中，而数据库的连接数只有10个，这时我们必须控制只有十个线程同时获取数据库连接保存数据，否则会报错无法获取数据库连接。这个时候，我们就可以使用Semaphore来做流控。（当资源不够时，线程会等待）

然后，可以在业务层捕获错误，并使用重试机制，保证请求都能正常返回数据。同时也限制了并发数。

```java
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
```

**注：release函数和acquire并没有要求一定是同一个线程都调用，可以A线程申请资源，B线程释放资源；调用release函数之前并没有要求一定要先调用acquire函数。**