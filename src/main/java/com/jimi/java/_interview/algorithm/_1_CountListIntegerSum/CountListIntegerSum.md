##  一个10w个整数的列表，充分利用cpu性能，尽可能快计算出列表的总和

### 分析

- __充分利用cpu性能__，所以考虑利用cpu多核，然后也就想到多线程。那想到多线程就涉及线程同步的问题。
- __尽可能快__，则需要考虑时间复杂度，则立马想到分治，但顺序循环的时间复杂度已经是O(n)了，所以分治并没优势。

### 思路

考虑利用多线程分段循环计算和，再计算总和，线程数为cpu个数，因线程切换cpu也很耗性能。

### 注意

- 线程个数：使用Runtime.getRuntime.availableProcessors()。
- 线程管理：使用线程池方式，如Executors.newFixedThreadPool(threadCount)。
- **线程同步控制的辅助类，包括CountDownLatch和CyclicBarrier，选择哪个？**

    答案是，使用CyclicBarrier。
    
    原因参考[CountDownLatch、CyclicBarrier和Semaphore的区别](CountDownLatch、CyclicBarrier和Semaphore的区别.md)

